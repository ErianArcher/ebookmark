package top.erian.ebookmark.model.entity;

import android.graphics.Bitmap;

/**
 * Created by root on 17-4-20.
 */

public class Book {
    private String bookName;
    private Bitmap cover;
    private int page;
    private int currentPage;

    public Book() {
        this.bookName = null;
        this.cover = null;
        this.page = -1;
        this.currentPage = -1;
    }

    public Book(String bookName, Bitmap cover, int page, int currentPage) {
        this.bookName = bookName;
        this.cover = cover;
        this.page = page;
        this.currentPage = currentPage;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", currentPage=" + currentPage +
                ", page=" + page +
                ", cover=" + cover +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return this.bookName.equals(((Book) o).bookName);
    }

    @Override
    public int hashCode() {
        int result = bookName.hashCode();
        return result;
    }

    /*These function is used to judge if the entity need to be updated*/
    public boolean samePage(Book b) {
        if (this.page == b.page) return true;
        else return false;
    }
    public boolean sameCover(Book b) {
        if (this.cover.equals(b.cover)) return true;
        else return false;
    }
    public boolean sameCurrentPage(Book b) {
        if (this.currentPage == b.currentPage) return true;
        else return false;
    }
    /*These function is used to judge if the fields are using default values*/
    public boolean isDefaultBookName() { return this.bookName == null;}
    public boolean isDefaultCover() { return this.cover == null; }
    public boolean isDefaultPage() { return this.page == -1; }
    public boolean isDefaultCurrentPage() { return this.currentPage == -1; }
}
