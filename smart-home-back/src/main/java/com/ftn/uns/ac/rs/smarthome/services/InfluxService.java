package com.ftn.uns.ac.rs.smarthome.services;

import com.ftn.uns.ac.rs.smarthome.models.Measurement;
import com.ftn.uns.ac.rs.smarthome.models.dtos.MeasurementsDTO;
import com.ftn.uns.ac.rs.smarthome.models.dtos.MeasurementsStreamRequestDTO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InfluxService {

    private final InfluxDBClient influxDbClient;

    public InfluxService(InfluxDBClient influxDbClient, Environment env) {
        this.influxDbClient = influxDbClient;
        this.bucket = env.getProperty("influxdb.bucket");
    }
    private final String bucket;

    public void save(String name, String value, Date timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();
        Point point = Point.measurement(name)
                .addTags(tags)
                .addField("value", value)
                .time(timestamp.toInstant(), WritePrecision.MS);
        writeApi.writePoint(point);
    }


    private MeasurementsDTO query(String fluxQuery, int maxData) {
        List<Measurement> result = new ArrayList<>();
        QueryApi queryApi = this.influxDbClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                Map<String,String> optTags = new HashMap<>();
                if(fluxRecord.getValueByKey("unit") != null)
                    optTags.put("unit", fluxRecord.getValueByKey("unit").toString());
                result.add(new Measurement(fluxRecord.getMeasurement(),
                        fluxRecord.getValue() == null ? 0 : ((double) fluxRecord.getValue()),
                        fluxRecord.getTime() == null ? null : Date.from(fluxRecord.getTime()),
                        optTags));
            }
        }
        boolean hasMore = false;
        if(result.size() > maxData) {
            hasMore = true;
            result.remove(result.size() - 1);
        }
        return new MeasurementsDTO(result,hasMore);
    }

    public MeasurementsDTO findPaginatedByMeasurementNameAndDeviceIdInTimeRange(MeasurementsStreamRequestDTO requestDTO) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "  |> range(start: %d, stop: %d) " +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"%s\") " +
                        "  |> filter(fn: (r) => r[\"_field\"] == \"value\") " +
                        "  |> filter(fn: (r) => r[\"deviceId\"] == \"%s\")" +
                        "  |> limit(n: %d, offset: %d) ",
                this.bucket,requestDTO.getFrom(),
                requestDTO.getTo(),
                requestDTO.getMeasurementName(),
                requestDTO.getDeviceId().toString(),
                requestDTO.getLimit() + 1,
                requestDTO.getOffset());
        return this.query(fluxQuery,requestDTO.getLimit());
    }
}
