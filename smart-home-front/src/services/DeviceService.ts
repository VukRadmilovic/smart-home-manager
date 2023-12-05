import axios from "axios";
import {DeviceDetailsDto} from "../models/DeviceDetailsDto.ts";

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
}