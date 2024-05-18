package app.entities;

import java.util.Objects;

public class Status {
    private int statusId;
    private String status;

    public Status(int statusId, String status) {
        this.statusId = statusId;
        this.status = status;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusId=" + statusId +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status status1)) return false;
        return statusId == status1.statusId && Objects.equals(status, status1.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusId, status);
    }
}
