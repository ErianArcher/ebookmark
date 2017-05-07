package top.erian.ebookmark.model.entity;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import top.erian.ebookmark.util.Stack;

/**
 * Created by root on 17-4-20.
 */

public class Book {
    private String bookName;
    private Bitmap cover;
    private int page;
    private Stack<Bookmark> bookmarks = new Stack<>();

    public Book() {
        this.bookName = null;
        this.cover = null;
        this.page = -1;
    }

    public Book(String bookName, Bitmap cover, int page, Bookmark... bookmarks) {
        this.bookName = bookName;
        this.cover = cover;
        this.page = page;
        addBookmark(bookmarks);
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

    public Bookmark[] getBookmarks() {
        return bookmarks.toArray(new Bookmark[1]);
    }

    public void addBookmark(Bookmark... bookmarks) {
        Collections.reverse(Arrays.asList(bookmarks));
        //This is a stack, when pushing an object into the stack it will be at the top,
        // but it will be covered by the coming one
        for (Bookmark bm:
                bookmarks) {
            this.bookmarks.push(bm);
        }
    }

    public void replaceBookmarks(Bookmark... bookmarks) {
        this.bookmarks.clear();
        this.addBookmark(bookmarks);
    }

    public boolean noBookmarks() {
        return bookmarks.empty();
    }

    public int getCurrentPage() {
        if (noBookmarks()) return 0;
        return bookmarks.peek().getCurrentPage();
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", page=" + page +
                ", cover=" + cover +
                ", bookmarks=" + bookmarks +
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
    public boolean sameBookmark(Book b) {
        /*If two entities are the same, then return true*/
        if (this == b) return true;

        List<Bookmark> thisBM = Arrays.asList(this.getBookmarks());
        List<Bookmark> bBM = Arrays.asList(b.getBookmarks());

        if (thisBM.size() != bBM.size()) return false;

        for (Bookmark bm:
             bBM) {
            int index = thisBM.indexOf(bBM);
            if (index == -1) return false;
            Bookmark sameInThisBM = thisBM.get(index);
            if (!sameInThisBM.sameCurrentPage(bm) ||
                    !sameInThisBM.sameNote(bm)) return false;
        }

        return true;
    }
}
