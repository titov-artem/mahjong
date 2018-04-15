export enum Wind {
    EAST = 'EAST',
    SOUTH = 'SOUTH',
    WEST = 'WEST',
    NORTH = 'NORTH',
}

export class WindHelper {
    static getOrdered(): Wind[] {
        return [Wind.EAST, Wind.SOUTH, Wind.WEST, Wind.NORTH];
    }
}