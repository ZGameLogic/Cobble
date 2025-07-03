package com.zgamelogic.data.history;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "history_birth")
public class BirthHistory extends History {
    private String name;
}
