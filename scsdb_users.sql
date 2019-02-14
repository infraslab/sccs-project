CREATE SEQUENCE users_info_seq;

CREATE OR REPLACE PACKAGE scs_user_security AS

  FUNCTION get_hash (p_username  IN  VARCHAR2,
                     p_password  IN  VARCHAR2)
    RETURN VARCHAR2;
    
  PROCEDURE add_user (p_username  IN  VARCHAR2,
                      p_password  IN  VARCHAR2,
                      p_email     IN  VARCHAR2,
                      p_ans_1     IN  VARCHAR2,
                      p_ans_2     IN  VARCHAR2,
                      p_ans_3     IN  VARCHAR2);

  PROCEDURE change_password (p_username      IN  VARCHAR2,
                             p_old_password  IN  VARCHAR2,
                             p_new_password  IN  VARCHAR2);

  PROCEDURE valid_user (p_username  IN  VARCHAR2,
                        p_password  IN  VARCHAR2);

  FUNCTION valid_user (p_username  IN  VARCHAR2,
                       p_password  IN  VARCHAR2)
    RETURN BOOLEAN;

END;

-- create body for scs_user_security

CREATE OR REPLACE PACKAGE BODY scs_user_security AS

  FUNCTION get_hash (p_username  IN  VARCHAR2,
                     p_password  IN  VARCHAR2)
    RETURN VARCHAR2 AS
    l_salt VARCHAR2(30) := 'SCSProjectSuperSalt';
  BEGIN
    RETURN DBMS_CRYPTO.HASH(UTL_RAW.CAST_TO_RAW(UPPER(p_username) || l_salt || UPPER(p_password)),DBMS_CRYPTO.HASH_SH1);
  END;

  PROCEDURE add_user (p_username  IN  VARCHAR2,
                      p_password  IN  VARCHAR2,
                      p_email     IN  VARCHAR2,
                      p_ans_1     IN  VARCHAR2,
                      p_ans_2     IN  VARCHAR2,
                      p_ans_3     IN  VARCHAR2) AS
  BEGIN
    INSERT INTO users_info (
      id,
      username,
      password,
      email,
      ANSWER_1,
      ANSWER_2,
      ANSWER_3
    )
    VALUES (
      users_info_seq.NEXTVAL,
      UPPER(p_username),
      get_hash(p_username, p_password),
      lower(p_email),
      UPPER(p_ans_1),
      UPPER(p_ans_2),
      UPPER(p_ans_3)
    );
    
    COMMIT;
  END;
   
  PROCEDURE change_password (p_username      IN  VARCHAR2,
                             p_old_password  IN  VARCHAR2,
                             p_new_password  IN  VARCHAR2) AS
    v_rowid  ROWID;
  BEGIN
    SELECT rowid
    INTO   v_rowid
    FROM   users_info
    WHERE  username = UPPER(p_username)
    AND    password = get_hash(p_username, p_old_password)
    FOR UPDATE;
    
    UPDATE users_info
    SET    password = get_hash(p_username, p_new_password)
    WHERE  rowid    = v_rowid;
    
    COMMIT;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20000, 'Invalid username/password.');
  END;

  PROCEDURE valid_user (p_username  IN  VARCHAR2,
                        p_password  IN  VARCHAR2) AS
    v_dummy  VARCHAR2(1);
  BEGIN
    SELECT '1'
    INTO   v_dummy
    FROM   users_info
    WHERE  username = UPPER(p_username)
    AND    password = get_hash(p_username, p_password);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20000, 'Invalid username/password.');
  END;
  
  FUNCTION valid_user (p_username  IN  VARCHAR2,
                       p_password  IN  VARCHAR2) 
    RETURN BOOLEAN AS
  BEGIN
    valid_user(p_username, p_password);
    RETURN TRUE;
  EXCEPTION
    WHEN OTHERS THEN
      RETURN FALSE;
  END;
  
END;


exec scs_user_security.add_user('test2','test', 'email@test','ans 1','ans 2','ans 3');


select * from users_info;

ALTER TABLE users_info ADD (
  CONSTRAINT users_info_pk UNIQUE (id)
);

ALTER TABLE users_info ADD (
  CONSTRAINT users_info_uk UNIQUE (username)
);