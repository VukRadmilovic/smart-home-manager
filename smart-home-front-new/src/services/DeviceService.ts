import axios from "axios";
import {DeviceDetailsDto} from "../models/DeviceDetailsDto.ts";
import {ChartBatch} from "../models/ChartBatch.ts";
import {MeasurementRequest} from "../models/MeasurementRequest.ts";

export class DeviceService {
    private api_host = "http://localhost:80"

    public getUserDevices(): Promise<DeviceDetailsDto[]> {
        return axios({
            method: 'GET',
            url: `${this.api_host}/api/devices/ownerAll`,
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }

    public getDeviceMeasurements( request: MeasurementRequest): Promise<ChartBatch> {
        return axios({
            method: 'PUT',
            url: `${this.api_host}/api/devices/measurements`,
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
            data: request
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }
}