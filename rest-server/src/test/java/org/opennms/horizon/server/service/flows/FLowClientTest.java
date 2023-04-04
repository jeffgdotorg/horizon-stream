/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.server.service.flows;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.opennms.dataplatform.flows.querier.v1.ApplicationSeriesRequest;
import org.opennms.dataplatform.flows.querier.v1.ApplicationSummariesRequest;
import org.opennms.dataplatform.flows.querier.v1.ApplicationsServiceGrpc;
import org.opennms.dataplatform.flows.querier.v1.Direction;
import org.opennms.dataplatform.flows.querier.v1.Exporter;
import org.opennms.dataplatform.flows.querier.v1.ExporterServiceGrpc;
import org.opennms.dataplatform.flows.querier.v1.Filter;
import org.opennms.dataplatform.flows.querier.v1.FlowingPoint;
import org.opennms.dataplatform.flows.querier.v1.ListRequest;
import org.opennms.dataplatform.flows.querier.v1.Series;
import org.opennms.dataplatform.flows.querier.v1.Summaries;
import org.opennms.dataplatform.flows.querier.v1.TrafficSummary;
import org.opennms.horizon.server.model.flows.RequestCriteria;
import org.opennms.horizon.server.model.flows.TimeRange;
import org.opennms.horizon.server.model.inventory.IpInterface;
import org.opennms.horizon.shared.constants.GrpcConstants;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;

public class FLowClientTest {
    @Rule
    public final GrpcCleanupRule grpcCleanUp = new GrpcCleanupRule();

    private static FlowClient flowClient;


    private final String tenantId = "testId";
    private final long exporterInterfaceId = 1L;
    private final String application = "http";
    private final int count = 20; // make sure it is not default value
    private final int bytesIn = 10;
    private final int bytesOut = 10;
    private final Instant startTime = Instant.now();
    private final Instant endTime = startTime.minus(1, ChronoUnit.HOURS);


    @Before
    public void startGrpc() throws IOException {
//        private static ApplicationsServiceGrpc.ApplicationsServiceImplBase applicationsServiceBlockingStub;
//        private static ExporterServiceGrpc.ExporterServiceImplBase exporterServiceStub;
        var applicationsServiceBlockingStub = mock(ApplicationsServiceGrpc.ApplicationsServiceImplBase.class, delegatesTo(
            new ApplicationsServiceGrpc.ApplicationsServiceImplBase() {
                @Override
                public void getApplications(ListRequest request, StreamObserver<org.opennms.dataplatform.flows.querier.v1.List> responseObserver) {
                    checkTimeRangeFilters(request.getFiltersList());
                    Assert.assertEquals(tenantId, request.getTenantId());
                    Assert.assertEquals(count, request.getLimit());
                    checkApplication(application, request.getFiltersList());
                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
                        responseObserver.onNext(org.opennms.dataplatform.flows.querier.v1.List.newBuilder().addElements(application).build());
                        responseObserver.onCompleted();
                    });
                }

                @Override
                public void getApplicationSummaries(ApplicationSummariesRequest request, StreamObserver<Summaries> responseObserver) {
                    checkTimeRangeFilters(request.getFiltersList());
                    Assert.assertEquals(tenantId, request.getTenantId());
                    Assert.assertEquals(count, request.getCount());
                    checkApplication(application, request.getFiltersList());
                    checkExporter(Exporter.newBuilder().setInterfaceId(exporterInterfaceId).build(), request.getFiltersList());
                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
                        responseObserver.onNext(Summaries.newBuilder()
                            .addSummaries(TrafficSummary.newBuilder().setApplication(application).setBytesIn(bytesIn).setBytesOut(bytesOut))
                            .build());
                        responseObserver.onCompleted();
                    });
                }

                @Override
                public void getApplicationSeries(ApplicationSeriesRequest request, StreamObserver<Series> responseObserver) {
                    checkTimeRangeFilters(request.getFiltersList());
                    Assert.assertEquals(tenantId, request.getTenantId());
                    Assert.assertEquals(count, request.getCount());
                    checkApplication(application, request.getFiltersList());
                    checkExporter(Exporter.newBuilder().setInterfaceId(exporterInterfaceId).build(), request.getFiltersList());

                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
                        responseObserver.onNext(Series.newBuilder()
                            .addPoint(FlowingPoint.newBuilder().setApplication(application).setValue(bytesIn).setDirection(Direction.INGRESS))
                            .build());
                        responseObserver.onCompleted();
                    });
                }
            }
        ));

        var exporterServiceStub = mock(ExporterServiceGrpc.ExporterServiceImplBase.class, delegatesTo(
            new ExporterServiceGrpc.ExporterServiceImplBase() {
                @Override
                public void getExporterInterfaces(ListRequest request, StreamObserver<org.opennms.dataplatform.flows.querier.v1.List> responseObserver) {
                    checkTimeRangeFilters(request.getFiltersList());
                    Assert.assertEquals(tenantId, request.getTenantId());
                    Assert.assertEquals(count, request.getLimit());
                    checkExporter(Exporter.newBuilder().setInterfaceId(exporterInterfaceId).build(), request.getFiltersList());

                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
                        responseObserver.onNext(org.opennms.dataplatform.flows.querier.v1.List.newBuilder()
                            .addElements(String.valueOf(exporterInterfaceId)).build());
                        responseObserver.onCompleted();
                    });
                }
            }
        ));

        grpcCleanUp.register(InProcessServerBuilder.forName(FLowClientTest.class.getName())
            .addService(applicationsServiceBlockingStub)
            .addService(exporterServiceStub)
            .directExecutor()
            .build()
            .start());
        ManagedChannel channel = grpcCleanUp.register(InProcessChannelBuilder.forName(FLowClientTest.class.getName())
            .directExecutor().build());
        flowClient = new FlowClient(channel, 600);
        flowClient.initialStubs();
    }


    @Test
    public void testGetApplications() {
        RequestCriteria requestCriteria = this.getRequestCriteria();

        var list = flowClient.findApplications(requestCriteria, tenantId);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(application, list.get(0));
    }

    @Test
    public void testGetExporters() {
        RequestCriteria requestCriteria = this.getRequestCriteria();

        var list = flowClient.findExporters(requestCriteria, tenantId);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(exporterInterfaceId, (long) list.get(0));
    }

    @Test
    public void testGetApplicationSummary() {
        RequestCriteria requestCriteria = this.getRequestCriteria();

        var summaries = flowClient.getApplicationSummaries(requestCriteria, tenantId);
        Assert.assertEquals(1, summaries.getSummariesCount());
        Assert.assertEquals(application, summaries.getSummaries(0).getApplication());
        Assert.assertEquals(bytesIn, summaries.getSummaries(0).getBytesIn());
        Assert.assertEquals(bytesOut, summaries.getSummaries(0).getBytesOut());
    }

    @Test
    public void testGetApplicationSeries() {
        RequestCriteria requestCriteria = this.getRequestCriteria();

        var series = flowClient.getApplicationSeries(requestCriteria, tenantId);
        Assert.assertEquals(1, series.getPointCount());
        Assert.assertEquals(application, series.getPoint(0).getApplication());
        Assert.assertEquals(bytesIn, series.getPoint(0).getValue(), 0);
        Assert.assertEquals(Direction.INGRESS, series.getPoint(0).getDirection());
    }

    private RequestCriteria getRequestCriteria() {
        RequestCriteria requestCriteria = new RequestCriteria();
        var timeRage = new TimeRange();
        timeRage.setStartTime(startTime);
        timeRage.setEndTime(endTime);
        requestCriteria.setTimeRange(timeRage);
        requestCriteria.setCount(count);

        // application
        List<String> applications = new ArrayList<>();
        applications.add(application);
        requestCriteria.setApplications(applications);

        // exporter
        org.opennms.horizon.server.model.flows.Exporter exporter = new org.opennms.horizon.server.model.flows.Exporter();
        IpInterface ipInterface = new IpInterface();
        ipInterface.setId(exporterInterfaceId);
        exporter.setIpInterface(ipInterface);
        List<org.opennms.horizon.server.model.flows.Exporter> exporters = new ArrayList<>();
        exporters.add(exporter);
        requestCriteria.setExporter(exporters);

        return requestCriteria;
    }

    private void checkTimeRangeFilters(List<Filter> filters) {
        var timeRangeFilters = filters.stream().filter(f -> f.hasTimeRange()).toList();
        Assert.assertEquals(1, timeRangeFilters.size());

        var startTimestamp = timeRangeFilters.get(0).getTimeRange().getStartTime();
        Assert.assertEquals(startTime, Instant.ofEpochSecond(startTimestamp.getSeconds(), startTimestamp.getNanos()));

        var endTimestamp = timeRangeFilters.get(0).getTimeRange().getEndTime();
        Assert.assertEquals(endTime, Instant.ofEpochSecond(endTimestamp.getSeconds(), endTimestamp.getNanos()));
    }

    private void checkApplication(String application, List<Filter> filters) {
        var applicationFilter = filters.stream().filter(f -> f.hasApplication()).toList();

        Assert.assertEquals(1, applicationFilter.size());
        Assert.assertEquals(application, applicationFilter.get(0).getApplication().getApplication());
    }

    private void checkExporter(Exporter exporter, List<Filter> filters) {
        var exporterFilters = filters.stream().filter(f -> f.hasExporter()).toList();

        Assert.assertEquals(1, exporterFilters.size());
        Assert.assertEquals(exporter.getInterfaceId(), exporterFilters.get(0).getExporter().getExporter().getInterfaceId());
    }
}