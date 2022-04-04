-- Table: AppUser
CREATE TABLE AppUser (
    Id serial  NOT NULL,
    Name varchar(50)  NOT NULL,
    Username varchar(50)  NOT NULL UNIQUE,
    Password varchar(60)  NOT NULL,
    Role_Id int  NOT NULL,
    CONSTRAINT AppUser_pk PRIMARY KEY (Id)
);

-- Table: Box
CREATE TABLE Box (
    Id serial  NOT NULL,
    Locker_Id int  NOT NULL,
    isUsed boolean  NOT NULL DEFAULT false,
    isOpen boolean  NOT NULL DEFAULT false,
    CONSTRAINT Box_pk PRIMARY KEY (Id)
);

-- Table: Locker
CREATE TABLE Locker (
    Id serial  NOT NULL,
    Name varchar(50)  NOT NULL UNIQUE,
    CONSTRAINT Locker_pk PRIMARY KEY (Id)
);

-- Table: Parcel
CREATE TABLE Parcel (
    Id serial  NOT NULL,
    Box_Id int  NOT NULL,
    User_Sender_Id int  NOT NULL,
    User_Recipient_Id int  NOT NULL,
    CreatedDate date  NOT NULL,
    isFinished boolean  NOT NULL DEFAULT false,
    CONSTRAINT Parcel_pk PRIMARY KEY (Id)
);

-- Table: UserRole
CREATE TABLE UserRole (
    Id serial  NOT NULL,
    Name varchar(50)  NOT NULL,
    CONSTRAINT UserRole_pk PRIMARY KEY (Id)
);

-- foreign keys
-- Reference: AppUser_Role (table: AppUser)
ALTER TABLE AppUser ADD CONSTRAINT AppUser_Role
    FOREIGN KEY (Role_Id)
    REFERENCES UserRole (Id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Lock_Locker (table: Box)
ALTER TABLE Box ADD CONSTRAINT Lock_Locker
    FOREIGN KEY (Locker_Id)
    REFERENCES Locker (Id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Rent_Lock (table: Parcel)
ALTER TABLE Parcel ADD CONSTRAINT Rent_Lock
    FOREIGN KEY (Box_Id)
    REFERENCES Box (Id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Rent_User_Recipient (table: Parcel)
ALTER TABLE Parcel ADD CONSTRAINT Rent_User_Recipient
    FOREIGN KEY (User_Recipient_Id)
    REFERENCES AppUser (Id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Rent_User_Sender (table: Parcel)
ALTER TABLE Parcel ADD CONSTRAINT Rent_User_Sender
    FOREIGN KEY (User_Sender_Id)
    REFERENCES AppUser (Id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;
