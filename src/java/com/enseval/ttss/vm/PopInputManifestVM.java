/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.vm;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.Customer;
import com.enseval.ttss.model.JenisLimbah;
import com.enseval.ttss.model.Manifest;
import com.enseval.ttss.model.PenandaTangan;
import com.enseval.ttss.model.Penerimaan;
import com.enseval.ttss.model.User;
import com.enseval.ttss.util.AuthenticationServiceImpl;
import com.enseval.ttss.util.MailNotif;
import com.enseval.ttss.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class PopInputManifestVM {

    @Wire("#input_manifest")
    private Window winInputManifest;

    Manifest manifest;
    User userLogin;
    List<Manifest> listNamaTeknik = new ArrayList<>();
    List<Manifest> listDriver = new ArrayList<>();
    List<Manifest> listNomorKendaraan = new ArrayList<>();
    List<Manifest> listPenandaTangan = new ArrayList<>();
    List<Manifest> listJabatanPenandaTangan = new ArrayList<>();
    List<Manifest> listJenisFisik = new ArrayList<>();

    boolean isEdit = false;
    boolean isDuplicate = false;

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("manifest") Manifest manifest,
            @ExecutionArgParam("duplicate") Boolean duplicate) {
        if (manifest != null && duplicate == null) {
            this.manifest = manifest;
            isEdit = true;

        } else if (duplicate != null && duplicate == true) {
            isDuplicate = true;
            this.manifest = new Manifest();
            this.manifest.setKodeManifest("JL ");
            this.manifest.setCustomerPenghasil(manifest.getCustomerPenghasil());
            this.manifest.setCustomerTujuan(manifest.getCustomerTujuan());
            this.manifest.setJabatanPenandaTangan(manifest.getJabatanPenandaTangan());
            this.manifest.setJenisFisik(manifest.getJenisFisik());
            this.manifest.setJenisLimbah(manifest.getJenisLimbah());
            this.manifest.setJmlBerat(manifest.getJmlBerat());
            this.manifest.setJmlKemasan(manifest.getJmlKemasan());
            this.manifest.setJmlKemasan2(manifest.getJmlKemasan2());
            this.manifest.setJmlKemasan3(manifest.getJmlKemasan3());
            this.manifest.setKarakteristikLimbah(manifest.getKarakteristikLimbah());
            this.manifest.setKetApprovalAkunting(manifest.getKetApprovalAkunting());
            this.manifest.setNamaDriver(manifest.getNamaDriver());
            this.manifest.setNamaTeknikLimbah(manifest.getNamaTeknikLimbah());
            this.manifest.setNomorKendaraan(manifest.getNomorKendaraan());
            this.manifest.setPenandaTangan(manifest.getPenandaTangan());
            this.manifest.setSatuanBerat(manifest.getSatuanBerat());
            this.manifest.setSatuanKemasan(manifest.getSatuanKemasan());
            this.manifest.setSatuanKemasan2(manifest.getSatuanKemasan2());
            this.manifest.setSatuanKemasan3(manifest.getSatuanKemasan3());
            this.manifest.setTglAngkut(manifest.getTglAngkut());
        } else {
            this.manifest = new Manifest();
            this.manifest.setKodeManifest("JL ");
        }
        this.userLogin = Ebean.find(User.class, new AuthenticationServiceImpl().getUserCredential().getUser().getId());
        this.listNamaTeknik = Ebean.find(Manifest.class).select("namaTeknikLimbah").setDistinct(true).findList();
        this.listDriver = Ebean.find(Manifest.class).select("namaDriver").setDistinct(true).findList();
        this.listNomorKendaraan = Ebean.find(Manifest.class).select("nomorKendaraan").setDistinct(true).findList();
        this.listPenandaTangan = Ebean.find(Manifest.class).select("penandaTangan").setDistinct(true).findList();
        this.listJabatanPenandaTangan = Ebean.find(Manifest.class).select("jabatanPenandaTangan").setDistinct(true).findList();
        this.listJenisFisik = Ebean.find(Manifest.class).select("jenisFisik").setDistinct(true).findList();

        Selectors.wireComponents(view, (Object) this, false);
    }

    @NotifyChange("manifest")
    public String getCustomerDetail() {
        return this.manifest.getCustomerPenghasil() == null ? "" : this.getManifest().getCustomerPenghasil().getAlamat() + "\ntelp : " + this.getManifest().getCustomerPenghasil().getNomorKontak();
    }

    @GlobalCommand
    @NotifyChange("manifest")
    public void setCustomer(@BindingParam("customer") Customer customer,
            @BindingParam("isPengirim") boolean isPengirim) {
        if (isPengirim) {
            this.manifest.setCustomerPenghasil(customer);
        } else {
            this.manifest.setCustomerTujuan(customer);
        }
    }

    @GlobalCommand
    @NotifyChange("manifest")
    public void setJenisLimbah(@BindingParam("jenisLimbah") JenisLimbah jenisLimbah) {
        this.manifest.setJenisLimbah(jenisLimbah);
    }

    @Command
    public void showCustomer(@BindingParam("isPengirim") boolean isPengirim) {
        Map m = new HashMap();
        m.put("isPengirim", isPengirim);
        Executions.createComponents("pop_customer.zul", null, m);
    }

    @Command
    public void showJenisLimbah() {
        Executions.createComponents("pop_jenis_limbah.zul", null, null);
    }

    @Command
    public void simpanManifest() {

        if (this.manifest.getKodeManifest().isEmpty() || this.manifest.getKodeManifest().toLowerCase().equals("jl ") || this.manifest.getKodeManifest().toLowerCase().equals("jl")) {
            Messagebox.show("Kode manifest belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

//        if (this.manifest.getNamaTeknikLimbah().isEmpty()) {
//            Messagebox.show("Nama teknik limbah belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
//            return;
//        }
        if (this.manifest.getCustomerPenghasil() == null) {
            Messagebox.show("Customer belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJenisFisik().isEmpty()) {
            Messagebox.show("Jenis fisik limbah belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJenisLimbah() == null) {
            Messagebox.show("Kode limbah belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getKarakteristikLimbah().isEmpty()) {
            Messagebox.show("Karakteristik belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getNamaTeknikLimbah().isEmpty()) {
            Messagebox.show("Nama teknik limbah belum diisi!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJmlKemasan() < 0L && this.manifest.getJmlKemasan2() < 0L && this.manifest.getJmlKemasan3() < 0L) {
            Messagebox.show("Jumlah kemasan masih kosong!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJmlKemasan() < 0L) {
            Messagebox.show("Jumlah kemasan 1 < 0!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJmlKemasan2() < 0L) {
            Messagebox.show("Jumlah kemasan 2 < 0!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJmlKemasan3() < 0L) {
            Messagebox.show("Jumlah kemasan 3 < 0!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

//        if (this.manifest.getJmlBerat() <= 0L) {
//            Messagebox.show("Jml berat masih kosong!", "Error", Messagebox.OK, Messagebox.ERROR);
//            return;
//        }
        if (this.manifest.getCustomerTujuan() == null) {
            Messagebox.show("Customer tujuan masih kosong!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (this.manifest.getJmlKemasan() > 0 && this.manifest.getJmlKemasan2() > 0) {
            if (this.manifest.getSatuanKemasan().toLowerCase().equals(this.manifest.getSatuanKemasan2().toLowerCase())) {
                Messagebox.show("Satuan kemasan tidak boleh ada yang sama!", "Error", Messagebox.OK, Messagebox.ERROR);
                return;
            }
        }

        if (this.manifest.getJmlKemasan2() > 0 && this.manifest.getJmlKemasan3() > 0) {
            if (this.manifest.getSatuanKemasan2().toLowerCase().equals(this.manifest.getSatuanKemasan3().toLowerCase())) {
                Messagebox.show("Satuan kemasan tidak boleh ada yang sama!", "Error", Messagebox.OK, Messagebox.ERROR);
                return;
            }
        }

        if (this.manifest.getJmlKemasan() > 0 && this.manifest.getJmlKemasan3() > 0) {
            if (this.manifest.getSatuanKemasan().toLowerCase().equals(this.manifest.getSatuanKemasan3().toLowerCase())) {
                Messagebox.show("Satuan kemasan tidak boleh ada yang sama!", "Error", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            
        }

        try {

            if (this.manifest.getPenerimaan() != null) {

                this.manifest.getPenerimaan().setJmlKemasan(this.manifest.getJmlKemasan());
                this.manifest.getPenerimaan().setJmlKemasan2(this.manifest.getJmlKemasan2());
                this.manifest.getPenerimaan().setJmlKemasan3(this.manifest.getJmlKemasan3());
                this.manifest.getPenerimaan().setSatuanKemasan(this.manifest.getSatuanKemasan());
                this.manifest.getPenerimaan().setSatuanKemasan2(this.manifest.getSatuanKemasan2());
                this.manifest.getPenerimaan().setSatuanKemasan3(this.manifest.getSatuanKemasan3());
                this.manifest.getPenerimaan().setJmlBerat(this.manifest.getJmlBerat());
                this.manifest.getPenerimaan().setSatuanBerat(this.manifest.getSatuanBerat());
                Ebean.update(this.manifest.getPenerimaan());
                Ebean.update(this.manifest);

            } else {
                Penerimaan p = new Penerimaan();
                p.setStatusPenerimaan("belum diterima");
                p.setManifest(this.manifest);
                p.setJmlKemasan(this.manifest.getJmlKemasan());
                p.setSatuanKemasan(this.manifest.getSatuanKemasan());
                p.setJmlKemasan2(this.manifest.getJmlKemasan2());
                p.setSatuanKemasan2(this.manifest.getSatuanKemasan2());
                p.setJmlKemasan3(this.manifest.getJmlKemasan3());
                p.setSatuanKemasan3(this.manifest.getSatuanKemasan3());
                p.setJmlBerat(this.manifest.getJmlBerat());
                p.setSatuanBerat(this.manifest.getSatuanBerat());
                Ebean.save(p);

                this.manifest.setUser(userLogin);
                this.manifest.setTglBuat(new Date());
                if (this.manifest.getStatusApproval().equals("rejected")) {
                    this.manifest.setStatusApproval("pending");
                }

                this.manifest.setStatusApproval("approved");
                this.manifest.setTglApprove(new Date());
                this.manifest.setPenerimaan(p);

                Ebean.save(this.manifest);
            }

            if (Util.setting("gmail_account").isEmpty() && Util.setting("gmail_password").isEmpty()
                    || Util.setting("gmail_account") != null && Util.setting("gmail_password") != null) {

                if (isEdit) {
                    if (Util.setting("mail_notif_update_manifest_active").equals("true")) {
                        final Manifest m = this.manifest;
                        Thread t = new Thread(new Runnable() {
                            Manifest manifest = m;

                            @Override
                            public void run() {
                                MailNotif mf = new MailNotif();
                                mf.emailUpdateManifest(this.manifest);
                            }
                        });
                        t.start();

                    }

                } else {
                    if (Util.setting("mail_notif_new_manifest_active").equals("true")) {
                        final Manifest m = this.manifest;
                        Thread t = new Thread(new Runnable() {
                            Manifest manifest = m;

                            @Override
                            public void run() {
                                MailNotif mf = new MailNotif();
                                mf.emailNewManifest(this.manifest);
                            }
                        });
                        t.start();

                    }
                }
            }

            BindUtils.postGlobalCommand((String) null, (String) null, "refresh", (Map) null);
            this.winInputManifest.detach();

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public Window getWinInputManifest() {
        return winInputManifest;
    }

    public void setWinInputManifest(Window winInputManifest) {
        this.winInputManifest = winInputManifest;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    public User getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(User userLogin) {
        this.userLogin = userLogin;
    }

    public List<Manifest> getListNamaTeknik() {
        return listNamaTeknik;
    }

    public void setListNamaTeknik(List<Manifest> listNamaTeknik) {
        this.listNamaTeknik = listNamaTeknik;
    }

    public List<Manifest> getListDriver() {
        return listDriver;
    }

    public void setListDriver(List<Manifest> listDriver) {
        this.listDriver = listDriver;
    }

    public List<Manifest> getListNomorKendaraan() {
        return listNomorKendaraan;
    }

    public void setListNomorKendaraan(List<Manifest> listNomorKendaraan) {
        this.listNomorKendaraan = listNomorKendaraan;
    }

    public List<Manifest> getListPenandaTangan() {
        return listPenandaTangan;
    }

    public void setListPenandaTangan(List<Manifest> listPenandaTangan) {
        this.listPenandaTangan = listPenandaTangan;
    }

    public List<Manifest> getListJabatanPenandaTangan() {
        return listJabatanPenandaTangan;
    }

    public void setListJabatanPenandaTangan(List<Manifest> listJabatanPenandaTangan) {
        this.listJabatanPenandaTangan = listJabatanPenandaTangan;
    }

    public boolean isIsEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public List<Manifest> getListJenisFisik() {
        return listJenisFisik;
    }

    public void setListJenisFisik(List<Manifest> listJenisFisik) {
        this.listJenisFisik = listJenisFisik;
    }

    public boolean isIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

}
