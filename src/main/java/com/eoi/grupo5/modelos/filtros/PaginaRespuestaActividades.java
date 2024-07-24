package com.eoi.grupo5.modelos.filtros;

import java.util.List;

public class PaginaRespuestaActividades<T> implements PaginaRespuesta<T>{

    private List<T> content;
    private int size;
    private long totalSize;
    private int page;
    private int totalPages;


    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void add(T element) {
        content.add(element);
    }
}
