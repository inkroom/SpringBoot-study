package com.inkbox.boot.demo.dos;

import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Table(name = "GOODS")
@Proxy(lazy = false)
public class GoodsDo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "GoodsDo{" +
                "id=" + id +
                ", count=" + count +
                '}';
    }
}
