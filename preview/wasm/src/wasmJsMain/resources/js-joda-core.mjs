export class ZoneId {
    id() { return "SYSTEM"; }
    static systemDefault() { return new ZoneId(); }
}

export const ZoneRulesProvider = {
    getTzdbData() { return { zones: [], links: [] }; }
};
