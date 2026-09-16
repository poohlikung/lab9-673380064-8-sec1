# Lab9 Spring Boot Transaction

นาย สรวิชญ์ วันเสน  
รหัสนักศึกษา 673380064-8  
Sec. 1

## รายละเอียด
REST API ระบบฝากเงิน ใช้ Spring Boot, JPA และ PostgreSQL

- สร้างบัญชีและดูข้อมูลบัญชี
- ฝากเงินพร้อมบันทึกประวัติ
- ใช้ @Transactional เพื่อ Rollback เมื่อเกิดข้อผิดพลาด

## วิธีรัน
1. สร้างฐานข้อมูลชื่อ lab9
2. ตั้งค่าฐานข้อมูลใน application.properties
3. รันคำสั่ง `.\mvnw.cmd spring-boot:run`

## API
- POST /accounts
- GET /accounts/{id}
- POST /accounts/{id}/deposit
