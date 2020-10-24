//GetMetricData.java
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.Dimension; //ADD
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit; //ADD
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetMetricData {

    public static void main(String[] args) {
        String InstanceId = args[0];  //ADD
        Region region = Region.US_EAST_1;
        CloudWatchClient cw = CloudWatchClient.builder()
                .region(region)
                .build();

        getMetData(cw, InstanceId) ;
    }

    // snippet-start:[cloudwatch.java2.get_metric_alarm.main]
    public static void getMetData( CloudWatchClient cw, String InstanceId) {//MOD

        try {
            // Set the date
            Instant start = Instant.ofEpochMilli(new Date().getTime());
            start = Instant.parse("2020-10-01T09:40:55Z"); //YOU NEED TO SET THIS DATA
            Instant endDate = Instant.now();
            Dimension dimensionsName = Dimension.builder().name("InstanceId").value(InstanceId).build(); //ADD

            Metric met = Metric.builder()
                    .metricName("DiskUtilization") //MOD
                    .namespace("CWAgent")  //MOD
                    .dimensions(dimensionsName)  //ADD
                    .build();

            MetricStat metStat = MetricStat.builder()
                    .stat("Average")//MOD: valid values are: SampleCount, Average, Sum, Minimum, Maximum
                    .period(60)
                    .metric(met)
                    .build();

            MetricDataQuery dataQUery = MetricDataQuery.builder()
                    .metricStat(metStat)
                    .id("foo2")
                    .returnData(true)
                    .build();

            List<MetricDataQuery> dq = new ArrayList();
            dq.add(dataQUery);

            GetMetricDataRequest getMetReq = GetMetricDataRequest.builder()
                .maxDatapoints(100)
                .startTime(start)
                .endTime(endDate)
                .metricDataQueries(dq)
                .build();

            GetMetricDataResponse response = cw.getMetricData(getMetReq);

            List<MetricDataResult> data = response.metricDataResults();

            for (int i = 0; i < data.size(); i++) {
                MetricDataResult item = (MetricDataResult) data.get(i);
                System.out.print("ID: "+ InstanceId);  //ADD
                System.out.print(", Namespace: CWAgent");  //ADD
                System.out.print(", MetricName: DiskUtilization");  //ADD
                System.out.println(", The status code is "+item.statusCode().toString());
                System.out.println("Object Data: \n "+item.toString());

                MetricDataResult item = (MetricDataResult) data.get(i);
                System.out.println("ID: "+ InstanceId);  //ADD
                System.out.println("Namespace: CWAgent");  //ADD
                System.out.println("MetricName: DiskUtilization");  //ADD
                //System.out.println("The label is "+item.label());
                System.out.println("The status code is "+item.statusCode().toString());
                System.out.println("Object Data: \n "+item.toString());

                //LIST_TIME
                List<Instant> dataPoint_time = item.timestamps();
                for (Object DataPoint_t : dataPoint_time) {
                    System.out.println(" VALUE: " + DataPoint_t .toString());
                }
                //LIST_DATA
                List<Double> dataPoint_val = item.values();
                for (Object dataPoint_v : dataPoint_val ) {
                    System.out.println(" VALUE: " + dataPoint_v.toString());
                }

            }

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}


