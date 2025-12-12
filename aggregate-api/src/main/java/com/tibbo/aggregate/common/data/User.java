package com.tibbo.aggregate.common.data;

import java.util.*;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.util.*;

public class User
{
  public static final String FIELD_NAME = "name";
  public static final String FIELD_FIRSTNAME = "firstname";
  public static final String FIELD_LASTNAME = "lastname";
  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_PASSWORD_EXPIRATION_DATE = "passwordExpirationDate";
  public static final String FIELD_INITIALIZATION_PASSWORD = "initializationPassword";
  public static final String FIELD_IGNORE_CONNECTION_MODE = "ignoreConnectionMode";
  public static final String FIELD_USE_EXTERNAL_AUTHENTICATION = "useExternalAuthentication";
  public static final String FIELD_COUNTRY = "country";
  public static final String FIELD_REGION = "region";
  public static final String FIELD_ZIP = "zip";
  public static final String FIELD_CITY = "city";
  public static final String FIELD_ADDRESS1 = "address1";
  public static final String FIELD_ADDRESS2 = "address2";
  public static final String FIELD_COMMENTS = "comments";
  public static final String FIELD_COMPANY = "company";
  public static final String FIELD_DEPARTMENT = "department";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_PHONE = "phone";
  public static final String FIELD_FAX = "fax";
  public static final String FIELD_TIMEZONE = "timezone";
  public static final String FIELD_LOCALE = "locale";
  public static final String FIELD_DATEPATTERN = "datepattern";
  public static final String FIELD_TIMEPATTERN = "timepattern";
  public static final String FIELD_WEEK_START = "weekStart";
  public static final String FIELD_CAPTCHA = "captcha";
  public static final String FIELD_EXTERNAL_USER = "externalUser";
  public static final String FIELD_PASSWORDS_TABLE = "passwordsTable";

  public final static String DEFAULT_ADMIN_USERNAME = "admin";
  public final static String DEFAULT_ADMIN_PASSWORD = "admin";
  
  public final static String DEFAULT_LOCALE = "en";
  
  private Long id;
  private String name;
  private String password;
  private Date passwordExpirationDate;
  private boolean initializationPassword;
  private boolean useExternalAuthentication;
  private String firstname;
  private String lastname;
  private String email;
  private String company;
  private String department;
  private String comments;
  private String timezone;
  private String phone;
  private String fax;
  private String address1;
  private String address2;
  private String city;
  private String region;
  private String zip;
  private String country;
  private String locale;
  private String datepattern;
  private String timepattern;
  private int weekStart;
  private boolean isPasswordUpdated;
  private boolean externalUser;
  private DataTable passwordsTable;

  public User()
  {
    locale = DEFAULT_LOCALE;
    datepattern = DateUtils.DEFAULT_DATE_PATTERN;
    timepattern = DateUtils.DEFAULT_TIME_PATTERN;
    weekStart = Calendar.MONDAY;
    isPasswordUpdated = true;
  }
  
  public String getAddress1()
  {
    return address1;
  }
  
  public String getAddress2()
  {
    return address2;
  }
  
  public String getCity()
  {
    return city;
  }
  
  public String getComments()
  {
    return comments;
  }
  
  public String getCompany()
  {
    return company;
  }
  
  public String getCountry()
  {
    return country;
  }
  
  public String getDepartment()
  {
    return department;
  }
  
  public String getEmail()
  {
    return email;
  }
  
  public String getFax()
  {
    return fax;
  }
  
  public String getFirstname()
  {
    return firstname;
  }
  
  public String getLastname()
  {
    return lastname;
  }
  
  public String getFullName()
  {
    return firstname + (lastname != null ? " " + lastname : "");
  }
  
  public String getPassword()
  {
    return password;
  }
  
  public String getPhone()
  {
    return phone;
  }
  
  public String getRegion()
  {
    return region;
  }
  
  public String getTimezone()
  {
    return timezone;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getZip()
  {
    return zip;
  }
  
  public Long getId()
  {
    return id;
  }
  
  public String getLocale()
  {
    return locale;
  }
  
  public String getDatepattern()
  {
    return datepattern;
  }
  
  public String getTimepattern()
  {
    return timepattern;
  }
  
  public void setZip(String zip)
  {
    this.zip = zip;
  }
  
  public void setName(String username)
  {
    this.name = username;
  }
  
  public void setTimezone(String timezone)
  {
    this.timezone = timezone;
  }
  
  public void setRegion(String region)
  {
    this.region = region;
  }
  
  public void setPhone(String phone)
  {
    this.phone = phone;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public void setLastname(String lastname)
  {
    this.lastname = lastname;
  }
  
  public void setFirstname(String firstname)
  {
    this.firstname = firstname;
  }
  
  public void setFax(String fax)
  {
    this.fax = fax;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public void setDepartment(String department)
  {
    this.department = department;
  }
  
  public void setCountry(String country)
  {
    this.country = country;
  }
  
  public void setCompany(String company)
  {
    this.company = company;
  }
  
  public void setComments(String comments)
  {
    this.comments = comments;
  }
  
  public void setCity(String city)
  {
    this.city = city;
  }
  
  public void setAddress2(String address2)
  {
    this.address2 = address2;
  }
  
  public void setAddress1(String address1)
  {
    this.address1 = address1;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public void setLocale(String locale)
  {
    this.locale = locale;
  }
  
  public void setDatepattern(String datepattern)
  {
    this.datepattern = datepattern;
  }
  
  public void setTimepattern(String timepattern)
  {
    this.timepattern = timepattern;
  }
  
  public int getWeekStart()
  {
    return weekStart;
  }
  
  public void setWeekStart(int weekStart)
  {
    this.weekStart = weekStart;
  }
  
  public Date getPasswordExpirationDate()
  {
    return passwordExpirationDate;
  }
  
  public void setPasswordExpirationDate(Date passwordExpirationDate)
  {
    this.passwordExpirationDate = passwordExpirationDate;
  }
  
  public boolean isInitializationPassword()
  {
    return initializationPassword;
  }

  public void setInitializationPassword(boolean initializationPassword)
  {
    this.initializationPassword = initializationPassword;
  }

  public boolean isPasswordUpdated()
  {
    return isPasswordUpdated;
  }
  
  public void setPasswordUpdated(boolean isPasswordUpdated)
  {
    this.isPasswordUpdated = isPasswordUpdated;
  }
  
  public boolean isUseExternalAuthentication()
  {
    return useExternalAuthentication;
  }
  
  public void setUseExternalAuthentication(boolean useExternalAuthentication)
  {
    this.useExternalAuthentication = useExternalAuthentication;
  }
  
  public boolean isExternalUser()
  {
    return externalUser;
  }
  
  public void setExternalUser(boolean externalUser)
  {
    this.externalUser = externalUser;
  }
  
  public DataTable getPasswordsTable()
  {
    return passwordsTable;
  }

  public void setPasswordsTable(DataTable passwordsTable)
  {
    this.passwordsTable = passwordsTable;
  }

  @Override
  public String toString()
  {
    return createDescription(firstname, lastname, name, true);
  }
  
  public static String createDescription(String firstname, String lastname, String username, boolean showUsername)
  {
    boolean hasFirst = firstname != null && firstname.length() > 0;
    boolean hasLast = lastname != null && lastname.length() > 0;
    
    String desc = username;
    
    if (hasFirst || hasLast)
    {
      if (showUsername)
      {
        desc += " (";
      }
      else
      {
        desc = "";
      }
      
      desc += hasFirst ? firstname : "";
      desc += hasLast ? (hasFirst ? " " : "") + lastname : "";
      
      if (showUsername)
      {
        desc += ")";
      }
    }
    
    return desc;
  }
}
