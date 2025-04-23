
---

# Employee Management System

A Jakarta EE 10 web application for managing employee records with full CRUD functionality.

---

## ✨ Features

- ✅ **Employee Management** – Add, edit, view, and delete employee records  
- 🔒 **Data Validation** – Client- and server-side form validation  
- 📱 **Responsive UI** – PrimeFaces-powered mobile-friendly interface  
- 🧠 **Session Management** – Keeps user data persistent during session  
- 💾 **MySQL Database** – Stores employee records persistently  
- ⚙️ **External Config** – DB config managed via a separate `.properties` file  

---

## 🛠️ Tech Stack

- **Jakarta EE 10** – Web framework  
- **JSF (Jakarta Faces)** – For component-based UI  
- **PrimeFaces 15.0.3** – Modern UI component library  
- **MySQL 9.0** – Relational database  
- **Payara Server 6** – Application server  
- **NetBeans IDE** – Development & deployment  
- **Ant** – Build tool

---

## Why Ant?

I chose Ant over Maven or Gradle for its simplicity and direct integration with NetBeans' classic Java Web project template. This avoids dependency configuration overhead on this simple, lightweight project.

---

## 📁 Project Structure

```
EmployeeManager/
├── src/
│   ├── java/
│   │   └── com/employee/
│   │       ├── Employee.java
│   │       ├── EmployeeBean.java
│   │       ├── EmployeeDAO.java
│   │       └── DatabaseConfig.java
│   ├── resources/
│   │   └── META-INF/beans.xml
│   └── webapp/
│       ├── index.xhtml
│       ├── home.xhtml
│       ├── resources/css/styles.css
│       └── WEB-INF/
│           ├── faces-config.xml
│           ├── web.xml
│           └── classes/database.properties
├── database/
│   └── employee_schema.sql
├── lib/
│   ├── mysql-connector-j-9.0.jar
│   └── primefaces-15.03-jakarta.jar
├── screenshots/
│   ├── add.PNG
│   ├── edit.PNG
│   ├── main.PNG
│   ├── delete.PNG
│   └── database.PNG
└── README.md
```

---

## 🚀 Setup and Deployment (NetBeans + Payara)

### 1. Clone the Repository

```bash
git clone https://github.com/ortupik/employeemanager.git
cd employeemanager
```

### 2. Create MySQL Database

You can run the provided sql db script or execute this:

```sql
CREATE DATABASE IF NOT EXISTS employeedb;
USE employeedb;

CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);
```

### 3. Configure `database.properties`

Edit the following:

```properties
db.url=jdbc:mysql://localhost:3306/employeedb?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Set Up Payara Server in NetBeans

1. Go to **Tools > Servers > Add Server**
2. Choose **Payara Server** and point to the install folder (e.g., `C:/payara6`)
3. Start Payara via `C:\payara6\bin\> asadmin start-domain`
4. Add JARs to your project:
   - `mysql-connector-j-9.0.jar`
   - `primefaces-15.03-jakarta.jar`

> ✅ Tip: You can keep the JARs in your `lib/` folder and add them via **Project Properties > Libraries** in NetBeans.

### 5. Build and Deploy

- Right-click the project → **Clean and Build**
- Right-click the project → **Run**

Visit: [http://localhost:8080/EmployeeManager](http://localhost:8080/EmployeeManager)

---

## 👨‍💼 How to Use

- **➕ Add** – Fill form → click *Add*  
- **✏️ Edit** – Click edit icon → update → click *Update*  
- **🗑️ Delete** – Click trash icon → confirm  
- **🔍 Filter/Sort** – Use search fields or column headers  

---

## 🧯 Troubleshooting

### Database not connecting?

- Check `database.properties` values  
- Make sure MySQL server is running  
- Ensure connector JAR is correctly linked  

### Deployment errors?

- Verify Payara is running  
- Inspect server logs at `domain1/logs/server.log`  
- Try Clean and Build again  

---

## 📄 License

Licensed under the [MIT License](LICENSE)

---
