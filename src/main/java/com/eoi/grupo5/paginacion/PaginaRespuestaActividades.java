package com.eoi.grupo5.paginacion;

import java.util.ArrayList;
import java.util.List;

public class PaginaRespuestaActividades<T> implements PaginaRespuesta<T>{

    private List<T> content = new ArrayList<>();
    private Integer size;
    private long totalSize;
    private Integer page;
    private Integer totalPages;


    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void setSize(Integer size) {
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
    public Integer getPage() {
        return page;
    }

    @Override
    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public Integer getTotalPages() {
        return totalPages;
    }

    @Override
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public void add(T element) {
        content.add(element);
    }

    // Determina si hay una página anterior
    public boolean hasPrevious() {
        return page > 0;
    }

    // Determina si hay una página siguiente
    public boolean hasNext() {
        return page < totalPages - 1;
    }
}
