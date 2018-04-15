export class JoinRequestRejectForm {

    id: number;
    reason: string;

    constructor(id: number, reason: string) {
        this.id = id;
        this.reason = reason;
    }
}
