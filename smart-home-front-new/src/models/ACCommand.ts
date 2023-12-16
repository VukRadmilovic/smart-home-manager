import {CommandType} from "./enums/CommandType.ts";
import {CommandParams} from "./CommandParams.ts";

export interface ACCommand {
    deviceId : number,
    commandType: CommandType,
    commandParams : CommandParams
}