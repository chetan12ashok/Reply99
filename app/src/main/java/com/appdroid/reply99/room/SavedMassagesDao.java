package com.appdroid.reply99.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavedMassagesDao {

    @Insert
    void insertMassage(MassageHolder massageHolder);

    @Query("SELECT * FROM massage")
    List<MassageHolder> getAllMassages();





    @Query("SELECT * FROM Conversion")
    List<ConversionHolder> getAllConversion();


    @Query("SELECT * FROM massage")
    LiveData<List<MassageHolder>> getAllUpdatedMassages();


    @Query("SELECT * FROM massage  WHERE whatsAppFlag = :flag ORDER BY massageId")
    LiveData<List<MassageHolder>> getWhatsAppMassages(boolean flag);

    @Query("SELECT * FROM massage  WHERE whatsAppFlag = :flag ORDER BY massageId")
    List<MassageHolder> getWhatsAppMassagesNormal(boolean flag);

    @Insert
    void insertConversion(ConversionHolder conversionHolder);


    @Insert
    void insertContactForIgnoreList(ContactHolder contactHolder);

    @Insert
    void insertContactForIgnoreList(List<ContactHolder> contactHolders);


    @Query("DELETE FROM contacts WHERE isIgnoreContact = :flag")
    void deleteContactsTableForIgnore(boolean flag);

    @Query("SELECT * FROM Conversion ORDER BY conversionId DESC LIMIT 1")
    ConversionHolder getLastConversion();

    @Query("SELECT * FROM Conversion  WHERE phoneNumber = :phoneNumber ORDER BY conversionId DESC LIMIT 1")
    ConversionHolder getLastConversionForThisNumber(String phoneNumber);


    @Query("UPDATE Conversion SET deliveryFlag=:flag WHERE conversionId = :id")
    void updateConversion(boolean flag, int id);

    @Query("SELECT * FROM Conversion  WHERE deliveryFlag = :flag ORDER BY conversionId")
    List<ConversionHolder> getCompletedConversions(boolean flag);

    @Query("SELECT * FROM Conversion  WHERE whatsAppDeliveryFlag = :flag ORDER BY conversionId")
    List<ConversionHolder> getWhatsAppConversions(boolean flag);


    @Query("SELECT * FROM contacts WHERE isIgnoreContact = :flag ORDER BY contactId")
    LiveData<List<ContactHolder>> getContactsForIgnoreList(boolean flag);

    @Query("SELECT * FROM contacts WHERE isIgnoreContact = :flag ORDER BY contactId")
    List<ContactHolder> getContactsList(boolean flag);



    @Query("UPDATE massage SET massage=:massage , whatsappImageLink=:whatsappImageLink WHERE massageId = :id")
    void updateMassage(String massage, int id,String whatsappImageLink);

    @Query("DELETE FROM massage WHERE massageId = :massageId")
    void deleteByPostDocId(int massageId);

    @Query("DELETE FROM contacts WHERE contactId = :contactId")
    void deleteSelectedContact(int contactId);


    @Query("SELECT EXISTS(SELECT * FROM contacts WHERE phoneNumber = :phoneNumber)")
    boolean isContactExistInIgnoreTable(String phoneNumber);


    @Delete
    void deleteConversion(ConversionHolder conversionHolder);
}
