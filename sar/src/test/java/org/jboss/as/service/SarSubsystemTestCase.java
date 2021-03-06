/*
* JBoss, Home of Professional Open Source.
* Copyright 2011, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.as.service;

import java.io.IOException;

import org.jboss.as.controller.ModelVersion;
import org.jboss.as.model.test.ModelTestControllerVersion;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.as.subsystem.test.KernelServicesBuilder;
import org.jboss.as.subsystem.test.LegacyKernelServicesInitializer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SarSubsystemTestCase extends AbstractSubsystemBaseTest {

    private static final AdditionalInitialization ADDITIONAL_INITIALIZATION = AdditionalInitialization.withCapabilities("org.wildfly.management.jmx");

    public SarSubsystemTestCase() {
        super(SarExtension.SUBSYSTEM_NAME, new SarExtension());
    }

    @Override
    protected AdditionalInitialization createAdditionalInitialization() {
        return ADDITIONAL_INITIALIZATION;
    }

    @Override
    protected String getSubsystemXml() throws IOException {
        return "<subsystem xmlns=\"urn:jboss:domain:sar:1.0\"/>";
    }

    @Override
    protected String getSubsystemXsdPath() throws Exception {
        return "schema/jboss-as-sar_1_0.xsd";
    }

    @Override
    protected String[] getSubsystemTemplatePaths() throws IOException {
        return new String[] {
               "/subsystem-templates/sar.xml"
        };
    }

    @Test
    public void testTransformersAS712() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.V7_1_2_FINAL, "1.0.0.Final");
    }

    @Test
    public void testTransformersAS713() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.V7_1_3_FINAL, "1.0.0.Final");
    }

    @Test
    public void testTransformersAS720() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.V7_2_0_FINAL, null);
    }

    @Test
    public void testTransformersEAP600() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.EAP_6_0_0, "1.0.0.Final-redhat-1");
    }

    @Test
    public void testTransformersEAP601() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.EAP_6_0_1, "1.0.0.Final-redhat-2");
    }

    @Test
    public void testTransformersEAP610() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.EAP_6_1_0, null);
    }

    @Test
    public void testTransformersEAP611() throws Exception {
        testTransformers_1_0_0(ModelTestControllerVersion.EAP_6_1_1, null);
    }

    private void testTransformers_1_0_0(ModelTestControllerVersion controllerVersion, String commonBeansVersion) throws Exception {
        ModelVersion modelVersion = ModelVersion.create(1, 0, 0);
        //Use the non-runtime version of the extension which will happen on the HC
        KernelServicesBuilder builder = createKernelServicesBuilder(ADDITIONAL_INITIALIZATION)
                .setSubsystemXml(getSubsystemXml());

        // Add legacy subsystems
        LegacyKernelServicesInitializer initializer = builder.createLegacyKernelServicesBuilder(null, controllerVersion, modelVersion)
                .addMavenResourceURL("org.jboss.as:jboss-as-sar:" + controllerVersion.getMavenGavVersion())
                .configureReverseControllerCheck(ADDITIONAL_INITIALIZATION, null);
        if (commonBeansVersion != null) {
            initializer.addMavenResourceURL("org.jboss.common:jboss-common-beans:" + commonBeansVersion);
        }

        KernelServices mainServices = builder.build();
        KernelServices legacyServices = mainServices.getLegacyServices(modelVersion);
        Assert.assertNotNull(mainServices);
        Assert.assertNotNull(legacyServices);
        checkSubsystemModelTransformation(mainServices, modelVersion);
    }
}
