export interface MeasurementRequest {
    from: number,
    to: number,
    limit: number,
    offset: number,
    deviceId: string,
    measurementName: string
}