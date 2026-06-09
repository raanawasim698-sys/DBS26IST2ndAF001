package domain;

import java.time.LocalDate;

public class Notice {

    private int noticeId;
    private String title;
    private String description;
    private int postedBy;
    private String postedByName; // joined field for display
    private LocalDate postDate;
    private LocalDate expiryDate;
    private String targetAudience; // All / Block A / Block B / etc.
    private boolean isActive;

    // ── Constructors ─────────────────────────────────────────
    public Notice() {}

    public Notice(int noticeId, String title, String description,
                  int postedBy, LocalDate postDate,
                  LocalDate expiryDate, String targetAudience, boolean isActive) {
        this.noticeId       = noticeId;
        this.title          = title;
        this.description    = description;
        this.postedBy       = postedBy;
        this.postDate       = postDate;
        this.expiryDate     = expiryDate;
        this.targetAudience = targetAudience;
        this.isActive       = isActive;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getNoticeId()                         { return noticeId; }
    public void setNoticeId(int noticeId)            { this.noticeId = noticeId; }

    public String getTitle()                         { return title; }
    public void setTitle(String title)               { this.title = title; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public int getPostedBy()                         { return postedBy; }
    public void setPostedBy(int postedBy)            { this.postedBy = postedBy; }

    public String getPostedByName()                  { return postedByName; }
    public void setPostedByName(String n)            { this.postedByName = n; }

    public LocalDate getPostDate()                   { return postDate; }
    public void setPostDate(LocalDate postDate)      { this.postDate = postDate; }

    public LocalDate getExpiryDate()                 { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate)  { this.expiryDate = expiryDate; }

    public String getTargetAudience()                { return targetAudience; }
    public void setTargetAudience(String t)          { this.targetAudience = t; }

    public boolean isActive()                        { return isActive; }
    public void setActive(boolean active)            { this.isActive = active; }

    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public String toString() { return title + " — " + targetAudience; }
}
