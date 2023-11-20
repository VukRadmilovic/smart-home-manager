import axios from "axios/index";
import {Token} from "../models/Token";
import {Property} from "../models/Property";

export class PropertyService {
    private api_host = "http://localhost:80"
    public async registerProperty(property: Property): Promise<void> {
        try {
            const response = await axios({
                method: 'POST',
                url: `${this.api_host}/api/property/registerProperty`,
                data: property
            });
            const token: Token = response.data;
            sessionStorage.setItem("user", token.token);
            sessionStorage.setItem("expiration", String(token.expiration));
        } catch (err) {
            console.log(err);
            throw err;
        }
    }
}