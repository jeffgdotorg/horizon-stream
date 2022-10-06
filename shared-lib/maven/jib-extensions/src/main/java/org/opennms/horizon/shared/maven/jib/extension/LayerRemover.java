package org.opennms.horizon.shared.maven.jib.extension;

import com.google.cloud.tools.jib.api.buildplan.ContainerBuildPlan;
import com.google.cloud.tools.jib.api.buildplan.ContainerBuildPlan.Builder;
import com.google.cloud.tools.jib.api.buildplan.LayerObject;
import com.google.cloud.tools.jib.maven.extension.JibMavenPluginExtension;
import com.google.cloud.tools.jib.maven.extension.MavenData;
import com.google.cloud.tools.jib.plugins.extension.ExtensionLogger;
import com.google.cloud.tools.jib.plugins.extension.JibPluginExtensionException;
import java.util.Map;
import java.util.Optional;

public class LayerRemover implements JibMavenPluginExtension<Object> {

  @Override
  public Optional<Class<Object>> getExtraConfigType() {
    return Optional.empty();
  }

  @Override
  public ContainerBuildPlan extendContainerBuildPlan(ContainerBuildPlan buildPlan, Map<String, String> properties, Optional<Object> extraConfig, MavenData mavenData, ExtensionLogger logger) throws JibPluginExtensionException {
      Builder plan = ContainerBuildPlan.builder()
          .setBaseImage(buildPlan.getBaseImage())
          .setPlatforms(buildPlan.getPlatforms())
          .setCreationTime(buildPlan.getCreationTime())
          .setFormat(buildPlan.getFormat())
          .setEnvironment(buildPlan.getEnvironment())
          .setLabels(buildPlan.getLabels())
          .setVolumes(buildPlan.getVolumes())
          .setExposedPorts(buildPlan.getExposedPorts())
          .setUser(buildPlan.getUser())
          .setWorkingDirectory(buildPlan.getWorkingDirectory())
          .setEntrypoint(buildPlan.getEntrypoint())
          .setCmd(buildPlan.getCmd());

      for (LayerObject layer : buildPlan.getLayers()) {
          //System.out.println(layer.getName() + " " + LayerType.valueOf(layer.getName()));
          if (layer.getName().toLowerCase().contains("extra")) {
              plan.addLayer(layer);
          }
      }

      return plan.build();
  }
}
