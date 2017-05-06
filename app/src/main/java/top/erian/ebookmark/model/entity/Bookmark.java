package top.erian.ebookmark.model.entity;

import java.util.Date;

/**
 * Created by root on 17-4-20.
 */

public class Bookmark {
    private int currentPage;
    private Date createDate;
    private String note; //TEXT type in SQLite

    public Bookmark(int currentPage, String note) {
        this.currentPage = currentPage;
        this.createDate = new Date();
        this.note = note;
    }

    public Bookmark() {
        this.currentPage = -1;
        this.createDate = new Date();
        this.note = null;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "currentPage=" + currentPage +
                ", createDate=" + createDate +
                ", note='" + note + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bookmark bookmark = (Bookmark) o;

        if (createDate != null ? !createDate.equals(bookmark.createDate) : bookmark.createDate != null)
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = createDate != null ? createDate.hashCode() : 0;
        return result;
    }

    /*These function is used to judge if the entity need to be updated*/
    public boolean sameCurrentPage(Bookmark bm) {
        return this.currentPage == bm.currentPage;
    }
    public boolean sameNote(Bookmark bm) {
        return this.note.equals(bm.note);
    }
}
