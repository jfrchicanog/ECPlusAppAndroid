package es.uma.ecplusproject.ecplusandroidapp.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by francis on 6/12/16.
 */
public abstract class DatabaseVersionChangeAdapter implements DatabaseVersionChange {
    protected int oldVersion;
    protected int newVersion;

    public DatabaseVersionChangeAdapter(int oldVersion, int newVersion) {
        setVersions(oldVersion, newVersion);
    }

    protected void setVersions(int oldVersion, int newVersion) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }

    protected int getOldVersion() {
        return oldVersion;
    }

    protected int getNewVersion() {
        return newVersion;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        checkUpgradeVersion(oldVersion, newVersion);
        upgrade(db);

    }
    private void checkUpgradeVersion(int oldVersion, int newVersion) {
        if (!fulfillsUpgradePrecondition(oldVersion, newVersion)) {
            throw new RuntimeException("Versions are not appropriate they are "+oldVersion+"->"+newVersion+", instead of "
                    +this.oldVersion+"->"+this.newVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        checkDowngradeVersion(oldVersion, newVersion);
        downgrade(db);
    }

    private void checkDowngradeVersion(int oldVersion, int newVersion) {
        if (!fulfillsDowngradePrecondition(oldVersion, newVersion)) {
            throw new RuntimeException("Versions are not appropriate they are "+oldVersion+"->"+newVersion+", instead of "
                    +this.newVersion+"->"+this.oldVersion);
        }
    }

    protected boolean fulfillsUpgradePrecondition(int oldVersion, int newVersion) {
        return this.oldVersion==oldVersion && this.newVersion==newVersion;
    }

    protected boolean fulfillsDowngradePrecondition(int oldVersion, int newVersion) {
        return this.newVersion==oldVersion && this.oldVersion==newVersion;
    }

    protected abstract void upgrade(SQLiteDatabase db);
    protected abstract void downgrade(SQLiteDatabase db);

}
