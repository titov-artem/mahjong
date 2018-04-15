export class PlayerShort {
    id: number;
    name: string;

    static of(id: number, name: string): PlayerShort {
        let player = new PlayerShort();
        player.id = id;
        player.name = name;
        return player;
    }
}