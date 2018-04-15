import {LeagueMemberInfo} from './league-member-info';

export class LeagueView {

    id: number;
    name: string;
    description: string;
    admins: number[];
    memberInfo: LeagueMemberInfo;
}
