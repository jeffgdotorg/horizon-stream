#!/bin/sh

helm upgrade -i opennms ./charts/opennms -f tilt-helm-values.yaml \
  --set Grafana.Image=opennms/horizon-stream-grafana-dev:local-basic \
  --set Keycloak.Image=opennms/horizon-stream-keycloak-dev:local-basic \
  --set OpenNMS.API.Image=opennms/horizon-stream-rest-server:local-basic \
  --set OpenNMS.Alarm.Image=opennms/horizon-stream-alarm:local-basic \
  --set OpenNMS.DataChoices.Image=opennms/horizon-stream-datachoices:local-basic \
  --set OpenNMS.Events.Image=opennms/horizon-stream-events:local-basic \
  --set OpenNMS.Inventory.Image=opennms/horizon-stream-inventory:local-basic \
  --set OpenNMS.MetricsProcessor.Image=opennms/horizon-stream-metrics-processor:local-basic \
  --set OpenNMS.Minion.Image=opennms/horizon-stream-minion:local-basic \
  --set OpenNMS.MinionGateway.Image=opennms/horizon-stream-minion-gateway:local-basic \
  --set OpenNMS.MinionGatewayGrpcProxy.Image=opennms/horizon-stream-minion-gateway-grpc-proxy:local-basic \
  --set OpenNMS.Notification.Image=opennms/horizon-stream-notification:local-basic \
  --set OpenNMS.UI.Image=opennms/horizon-stream-ui:local-basic \

