package com.cs360.cs_360inventorytracker;

/*
 * This interface is an intermediary between the SQLite database for accounts and the rest of the code.
 * It makes the code used in the rest of the application much easier to manage.
 */
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AccountDao {

    @Query("SELECT * FROM accounts WHERE username = :username")
    public Account getAccount(String username);

    @Query("SELECT * FROM accounts ORDER BY username")
    public List<Account> getAccounts();

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertAccount(Account account);

    @Update
    public void updateAccount(Account account);

    @Delete
    public void deleteAccount(Account account);
}
