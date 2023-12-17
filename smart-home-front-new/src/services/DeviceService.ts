import axios from "axios";
import {DeviceDetailsDto} from "../models/DeviceDetailsDto.ts";
import {MeasurementRequest} from "../models/MeasurementRequest.ts";
import {ChartData} from "../models/ChartData.ts";
import EventSource from "react-native-sse";
import {DeviceCapabilities} from "../models/DeviceCapabilities.ts";
import {CommandsDTO} from "../models/CommandsDTO.ts";

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

    public getDeviceMeasurements(request: MeasurementRequest): Promise<ChartData[]> {
        const result : ChartData[] = [];
        const eventSourceHeader = {headers: { 'Authorization': 'Bearer ' + sessionStorage.getItem('user')}};
        const promise : Promise<ChartData[]> = new Promise((resolve) => {
            const  url = `${this.api_host}/api/devices/measurements?from=${request.from}&to=${request.to}&deviceId=${request.deviceId}&measurement=${request.measurementName}`
            const  es = new EventSource(url, eventSourceHeader);
            es.addEventListener("message", (event) => {
                result.push(...JSON.parse(event.data as string))
                es.close();

            });
            es.addEventListener("close", () => {
                resolve(result)
            });
        })
        return promise.then((val) => {
            return val})
    }

    public getDeviceCapabilities(deviceId: number) : Promise<DeviceCapabilities> {
        return axios({
            method: 'GET',
            url: `${this.api_host}/api/devices/capabilities/` + deviceId,
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }

    public getPaginatedCommands(deviceId: number, from: number, to: number,page:number, size: number, userId: number) : Promise<CommandsDTO> {
        return axios({
            method: 'GET',
            url : `${this.api_host}/api/devices/commands?from=${from}&to=${to}&deviceId=${deviceId}&page=${page}&size=${size}&userId=${userId}`,
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }
}