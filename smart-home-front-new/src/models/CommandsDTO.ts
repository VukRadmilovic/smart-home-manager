import {CommandSummary} from "./CommandSummary.ts";

export interface CommandsDTO {
    commands: CommandSummary[],
    allUsers: string[]
}