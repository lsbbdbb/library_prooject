package com;

import java.sql.Date;

public class BorrowRecord {
    private int recordId;
    private String bookTitle;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private String status;

    public BorrowRecord(int recordId, String bookTitle, Date borrowDate, Date dueDate, Date returnDate, String status) {
        this.recordId = recordId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getTitle() {  // 修正这里
        return bookTitle;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOverdue() {
        return "逾期".equals(status);
    }
}
