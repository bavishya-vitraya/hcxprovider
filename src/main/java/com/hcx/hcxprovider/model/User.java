package com.hcx.hcxprovider.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Users")
public class User {

    @Id
    private String id;
    private String userName;
    private String password;

}
