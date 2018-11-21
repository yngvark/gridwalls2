import {Coordinate} from "./Coordinate";

export class ZombieMoved {
    readonly id:string;
    readonly coordinate:Coordinate;

    constructor(id:string, coordinate:Coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }
}