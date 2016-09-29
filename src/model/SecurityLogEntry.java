package model;

import java.util.Date;

class SecurityLogEntry {
    enum EventType {
        LOGIN_ATTEMPT("LOGIN ATTEMPT"),
        ACCOUNT_DELETE("ACCOUNT DELETE"),
        USER_BAN("USER BAN"),
        UNBLOCK_ACCOUNT("UNBLOCK ACCOUNT"),
        REPORT_DELETE("REPORT DELETE");
        private String name;
        EventType(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }
    enum EventStatus {
        SUCCESS("SUCCESS"),
        INVALID_USER("INVALID USER"),
        INVALID_PASS("INVALID PASS");
        private String name;
        EventStatus(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }
    private Date timestamp;
    private Integer issuerId;
    private Integer objectId;
    private EventType eventType;
    //0 = Success
    //1 = Invalid ID
    //2 = Invalid password
    private EventStatus eventStatus;

    static SecurityLogEntry loginAttempt(Integer userId, EventStatus eventStatus) {
        return new SecurityLogEntry(null, userId, EventType.LOGIN_ATTEMPT, eventStatus);
    }

    static SecurityLogEntry acountDelete(Integer issuerId, Integer userId) {
        return new SecurityLogEntry(issuerId, userId, EventType.ACCOUNT_DELETE, null);
    }

    static SecurityLogEntry userBan(Integer issuerId, Integer userId) {
        return new SecurityLogEntry(issuerId, userId, EventType.USER_BAN, null);
    }

    static SecurityLogEntry unblockAccount(Integer issuerId, Integer userId) {
        return new SecurityLogEntry(issuerId, userId, EventType.UNBLOCK_ACCOUNT, null);
    }

    static SecurityLogEntry reportDelete(Integer issuerId, Integer reportId) {
        return new SecurityLogEntry(issuerId, reportId, EventType.REPORT_DELETE, null);
    }
    

    private SecurityLogEntry(Integer issuerId, Integer objectId, EventType eventType, EventStatus eventStatus) {
        timestamp = new Date();
        this.issuerId = issuerId;
        this.objectId = objectId;
        this.eventType = eventType;
        this.eventStatus = eventStatus;
        //System.out.println(this); //For debugging
    }

    @Override
    public String toString() {
        if (this.eventType == EventType.LOGIN_ATTEMPT) {
            return this.eventType.toString() +
                    "\nTimestamp: " + this.timestamp.toString() +
                    "\nUser ID: " + this.objectId +
                    "\nStatus: " + this.eventStatus.toString();
        } else {
            //TODO
            return eventType.toString();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash += this.timestamp.hashCode();
        hash += this.issuerId == null ? 0 : this.issuerId.hashCode() * 3;
        hash += this.objectId == null ? 0 : this.objectId.hashCode() * 5;
        hash += this.eventType == null ? 0 : this.eventType.hashCode() * 7;
        hash += this.eventStatus == null ? 0 : this.eventStatus.hashCode() * 11;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!SecurityLogEntry.class.isAssignableFrom(obj.getClass())) return false;
        final SecurityLogEntry that = (SecurityLogEntry) obj;
        return !(!this.timestamp.equals(that.timestamp) || !this.issuerId.equals(that.issuerId) ||
                !this.objectId.equals(that.objectId) || !this.eventType.equals(that.eventType) ||
                !this.eventStatus.equals(that.eventStatus));
    }
}
