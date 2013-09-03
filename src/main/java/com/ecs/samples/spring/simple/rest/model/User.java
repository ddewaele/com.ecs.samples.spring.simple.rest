//
//  2013 (c) eCommit Consulting Services BVBA 
//  All Rights Reserved.
// 
// NOTICE:  All information contained herein is, and remains
// the property of eCommit Consulting Services BVBA
// 
// The intellectual and technical concepts contained
// herein are proprietary to eCommit Consulting Services BVBA
// and may be covered by U.S. and Foreign Patents,
// patents in process, and are protected by trade secret or copyright law.
// 
// Dissemination of this information or reproduction of this material
// is strictly forbidden unless prior written permission is obtained
// from eCommit Consulting Services BVBA.
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.
//
package com.ecs.samples.spring.simple.rest.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "users")
@SuppressWarnings("serial")
public class User implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    private String email;
    private String password;
    private boolean registered;
    
    
	private String authority = "user";
    
     
    public User()
    {
    }
    
    public User(String email) {
    	this.email = email;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getEmail() {
		return email;
	}
    
    public void setEmail(String email) {
		this.email = email;
	}

    public String getPassword() {
		return password;
	}
    
    public void setPassword(String password) {
		this.password = password;
	}
    
    public String getAuthority() {
		return authority;
	}
    
    public void setAuthority(String authority) {
		this.authority = authority;
	}
 
    public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

    @Override
    public String toString() {
    	return "[User:" + this.id + "|" + this.email + "|" + this.password + "|" +  this.authority + "]";
    }
    
}