/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.dag.nodes;

import org.terasology.assets.ResourceUrn;
import org.terasology.config.Config;
import org.terasology.config.RenderingConfig;
import org.terasology.context.Context;
import org.terasology.rendering.opengl.FBOConfig;
import org.terasology.rendering.opengl.fbms.DisplayResolutionDependentFBOs;


/**
 * This class is a thin facade in front of the BlurNode class it inherits from.
 *
 * Instances of this class specialize the blur operation to render a "Haze" layer,
 * combined later in the pipeline to progressively fade the rendered world into
 * the backdrop.
 *
 * I.e. if the sky is pink at sunset, faraway hills will fade into pink as they get
 * further away from the camera.
 */
public class HazeNode extends BlurNode {
    public static final ResourceUrn INTERMEDIATE_HAZE = new ResourceUrn("engine:intermediateHaze");
    public static final ResourceUrn FINAL_HAZE = new ResourceUrn("engine:finalHaze");
    private static final float BLUR_RADIUS = 8.0f;

    private DisplayResolutionDependentFBOs displayResolutionDependentFBOs;
    private Config config;

    private RenderingConfig renderingConfig;

    /**
     * Initializes the HazeNode instance.
     *
     * @param inputConfig an FBOConfig describing the input FBO, to be retrieved from an injected DisplayResolutionDependentFBOs instance.
     * @param outputConfig an FBOConfig describing the output FBO, to be retrieved from an injected DisplayResolutionDependentFBOs instance.
     * @param aLabel a String to label the instance's entry in output generated by the PerformanceMonitor
     */
    public HazeNode(Context context, FBOConfig inputConfig, FBOConfig outputConfig, String aLabel) {
        super(context, inputConfig, outputConfig, context.get(DisplayResolutionDependentFBOs.class), BLUR_RADIUS, aLabel);
    }

    /**
     * This method establishes the conditions in which the blur will take place, by enabling or disabling the node.
     *
     * In this particular case the node is enabled if RenderingConfig.isInscattering() returns true.
     */
    @Override
    protected void setupConditions(Context context) {
        renderingConfig = context.get(Config.class).getRendering();
        renderingConfig.subscribe(RenderingConfig.INSCATTERING, this);
        requiresCondition(() -> renderingConfig.isInscattering());
    }
}
