package org.example.expensestrack.Model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Fixes applied:
 * 1. Added `localId` column — SettingsRepository.existsByLocalId() and
 *    findByLocalId() referenced it but the entity didn't have the field,
 *    causing a Hibernate mapping exception at startup.
 * 2. Added `transactionType` — SettingsService.syncCategories() calls
 *    settings.setTransactionType() and SettingsRepository has
 *    findAllByUserIdAndTransactionType(), neither of which worked without it.
 * 3. Kept all existing user-profile fields (name, icon, email, darkTheme,
 *    customCategories) intact — they are still used by the Android SettingsDTO.
 *
 * Note: The Settings entity is used for two purposes in this codebase —
 * user profile data AND expense categories. If you want to separate them
 * in the future, create a Category entity and a UserProfile entity.
 */
@Entity
@Table(name = "settings", indexes = {
        @Index(columnList = "localId", unique = true),
        @Index(columnList = "userId")
})
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Offline-generated UUID from Android — used for deduplication on sync. */
    @Column(unique = true)
    private String localId;

    @Column(nullable = false)
    private String userId;

    private String name;
    private String icon;
    private String email;
    private Boolean darkTheme;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ElementCollection
    @CollectionTable(
            name        = "settings_custom_categories",
            joinColumns = @JoinColumn(name = "settings_id")
    )
    @MapKeyColumn(name = "category_key")
    @Column(name = "category_value")
    private Map<String, String> customCategories = new HashMap<>();

    public Settings() {}

    public Settings(String userId, String name, String icon, String email,
                    Boolean darkTheme, Map<String, String> customCategories) {
        this.userId           = userId;
        this.name             = name;
        this.icon             = icon;
        this.email            = email;
        this.darkTheme        = darkTheme;
        this.customCategories = customCategories;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }

    public String getLocalId()                           { return localId; }
    public void setLocalId(String localId)               { this.localId = localId; }

    public String getUserId()                            { return userId; }
    public void setUserId(String userId)                 { this.userId = userId; }

    public String getName()                              { return name; }
    public void setName(String name)                     { this.name = name; }

    public String getIcon()                              { return icon; }
    public void setIcon(String icon)                     { this.icon = icon; }

    public String getEmail()                             { return email; }
    public void setEmail(String email)                   { this.email = email; }

    public Boolean getDarkTheme()                        { return darkTheme; }
    public void setDarkTheme(Boolean darkTheme)          { this.darkTheme = darkTheme; }

    public TransactionType getTransactionType()          { return transactionType; }
    public void setTransactionType(TransactionType t)    { this.transactionType = t; }

    public Map<String, String> getCustomCategories()     { return customCategories; }
    public void setCustomCategories(Map<String, String> m) { this.customCategories = m; }
}