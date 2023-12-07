import axios from "axios";
import {Property} from "../models/Property";


export class PropertyService {
    private api_host = "http://localhost:80"
    public getAllPoperty():Promise<Property[]>{
        return axios({
            method: 'GET',
            url: `${this.api_host}/api/property/getAllProperty`,
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }
    public getProperty():Promise<Property[]>{
        return axios({
            method: 'GET',
            url: `${this.api_host}/api/property/getProperty/`+sessionStorage.getItem('username'),
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }
    public async registerProperty(property: Property): Promise<string> {
        return axios({
            method: 'POST',
            url: `${this.api_host}/api/property/registerProperty`,
            data: property,
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => response.data
        ).catch((err) => {
            throw err
        });
    }
}