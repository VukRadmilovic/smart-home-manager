import axios from "axios";
import {DeviceDetailsDto} from "../models/DeviceDetailsDto.ts";
import {MeasurementRequest} from "../models/MeasurementRequest.ts";
import {ChartData} from "../models/ChartData.ts";

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

    public getDeviceMeasurements( request: MeasurementRequest): Promise<ChartData[]> {
        const result : ChartData[] = [];
        return axios({
            method: 'PUT',
            url: `${this.api_host}/api/devices/measurements`,
            responseType: 'stream',
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user'),

            },
            data: request,
            onDownloadProgress: (evt) => {
                const batches : Array<ChartData[]> = JSON.parse(evt.event.target.response)
                batches.forEach((batch) => {
                    result.push(...batch)
                })
            }
        }).then(() =>  result
        ).catch((err) => {
            throw err
        });
    }
}