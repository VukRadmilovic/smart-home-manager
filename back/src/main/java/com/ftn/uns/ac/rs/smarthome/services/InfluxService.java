package com.ftn.uns.ac.rs.smarthome.services;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class InfluxService {

    private final InfluxDBClient influxDbClient;

    public InfluxService(InfluxDBClient influxDbClient) {
        this.influxDbClient = influxDbClient;
    }

    /**
     * Saves a record to the InfluxDb.
     * @param name -
     * @param value
     * @param timestamp
     * @param tags
     */
    public void save(String name, float value, Date timestamp, Map<String, String> tags) {
        WriteApiBlocking writeApi = this.influxDbClient.getWriteApiBlocking();

        Point point = Point.measurement(name)
                .addTags(tags)
                .addField("value", value)
                .time(timestamp.toInstant(), WritePrecision.MS);
        writeApi.writePoint(point);
    }
}
