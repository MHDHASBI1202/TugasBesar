import java.util.Scanner; // Menggunakan Scanner untuk input
import java.util.TreeMap; // Menggunakan Collection Framework
import java.util.Date; // Menggunakan Date
import java.text.SimpleDateFormat; // Menggunakan SimpleDateFormat

// Enum untuk Role
enum Role {
    JUNGLER, EXPLANER, GOLDLANER, MIDLANER, ROAMER; // Enum untuk role pemain

    public static Role fromInt(int choice) { 
        switch (choice) { //Percabangan
            case 1: return JUNGLER;
            case 2: return EXPLANER;
            case 3: return GOLDLANER;
            case 4: return MIDLANER;
            case 5: return ROAMER;
            default: throw new IllegalArgumentException("Role tidak valid."); //Exception Handling
        }
    }
}

// Interface untuk Pemain
interface IPemain {
    void setRating(double ratingMikro, double ratingMakro, double ratingHeroPool, double ratingAttitude);
    double hitungRerataRating();
    Role getRole();
    String getUid();
    String getStatus();
    String getNama();
    String getIgn();
}

// Kelas Pemain sebagai kelas utama
abstract class Pemain implements IPemain {
    protected String uid;
    protected String nama;
    protected String ign;
    protected Role role;
    protected double ratingMikro;
    protected double ratingMakro;
    protected double ratingHeroPool;
    protected double ratingAttitude;
    protected String status;

    public Pemain(String uid, String nama, String ign, Role role) {
        this.uid = uid;
        this.nama = nama;
        this.ign = ign;
        this.role = role;
        this.status = "Pemain";
    }

    @Override
    public void setRating(double ratingMikro, double ratingMakro, double ratingHeroPool, double ratingAttitude) {
        this.ratingMikro = ratingMikro;
        this.ratingMakro = ratingMakro;
        this.ratingHeroPool = ratingHeroPool;
        this.ratingAttitude = ratingAttitude;
        updateStatus();
    }

    private void updateStatus() {
        double rerataRating = hitungRerataRating();
        if (!this.status.equals("Pemain Inti")) { //Percabangan
            if (rerataRating > 7.5) { //Percabangan
                this.status = "Pemain Inti";
            } else {
                this.status = "Pemain Cadangan";
            }
        }
    }

    @Override
    public double hitungRerataRating() { // Perhitungan Matematika
        return (ratingMikro + ratingMakro + ratingHeroPool + ratingAttitude) / 4;
    }

    // Implementasi method dari interface
    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getNama() {
        return nama;
    }

    @Override
    public String getIgn() {
        return ign;
    }

    @Override
    public String toString() {
        return "UID: " + uid + "\n" +
               "Nama: " + nama + "\n" +
               "IGN: " + ign + "\n" +
               "Role: " + role + "\n" +
               "Rerata Rating: " + hitungRerataRating() + "\n" +
               "Status: " + status;
    }
}

// Kelas PemainInti Subclass dari Pemain
class PemainInti extends Pemain {
    public PemainInti(String uid, String nama, String ign, Role role) {
        super(uid, nama, ign, role);
        this.status = "Pemain Inti";
    }
}

// Kelas PemainCadangan Subclass dari Pemain
class PemainCadangan extends Pemain {
    public PemainCadangan(String uid, String nama, String ign, Role role) {
        super(uid, nama, ign, role);
        this.status = "Pemain Cadangan";
    }
}

// Kelas Tim untuk mengelola pemain
class Tim {
    private TreeMap<String, Pemain> pemainMap; // Collection untuk menyimpan pemain
    private TreeMap<Role, Pemain> pemainIntiPerRole; // Collection untuk menyimpan pemain inti per role

    public Tim() { //Constructor
        pemainMap = new TreeMap<>();
        pemainIntiPerRole = new TreeMap<>();
    }

    public TreeMap<String, Pemain> getPemainMap() {
        return pemainMap;
    }

    public void tambahPemain(String uid, String nama, String ign, Role role, Scanner scanner) {
        if (pemainMap.containsKey(uid)) {
            throw new IllegalArgumentException("\nPemain dengan UID " + uid + " sudah ada.");
        }

        Pemain pemain = new PemainCadangan(uid, nama, ign, role);
        double ratingMikro = getValidRating(scanner, "Masukkan Rating Mikro (1-10): ");
        double ratingMakro = getValidRating(scanner, "Masukkan Rating Makro (1-10): ");
        double ratingHeroPool = getValidRating(scanner, "Masukkan Rating Hero Pool (1-10): ");
        double ratingAttitude = getValidRating(scanner, "Masukkan Rating Attitude (1-10): ");

        pemain.setRating(ratingMikro, ratingMakro, ratingHeroPool, ratingAttitude);

        for (Pemain existingPlayer : pemainMap.values()) {
            if (existingPlayer.getRole() == role) {
                if (pemain.hitungRerataRating() > existingPlayer.hitungRerataRating()) {
                    existingPlayer.status = "Pemain Cadangan";
                }
            }
        }

        if (pemainIntiPerRole.containsKey(role)) {
            Pemain pemainIntiLama = pemainIntiPerRole.get(role);
            if (pemain.hitungRerataRating() > pemainIntiLama.hitungRerataRating()) {
                pemain.status = "Pemain Inti";
                pemainIntiLama.status = "Pemain Cadangan";
                pemainIntiPerRole.put(role, pemain);
            } else {
                pemain.status = "Pemain Cadangan";
            }
        } else {
            pemain.status = "Pemain Inti";
            pemainIntiPerRole.put(role, pemain);
        }

        pemainMap.put(uid, pemain);
        System.out.println("\nPemain berhasil ditambahkan.");
        System.out.println(pemain);
    }

    public void updateRatingPemain(Pemain pemain, Scanner scanner) {
        double ratingMikro = getValidRating(scanner, "Masukkan Rating Mikro (1-10): ");
        double ratingMakro = getValidRating(scanner, "Masukkan Rating Makro (1-10): ");
        double ratingHeroPool = getValidRating(scanner, "Masukkan Rating Hero Pool (1-10): ");
        double ratingAttitude = getValidRating(scanner, "Masukkan Rating Attitude (1-10): ");
        pemain.setRating(ratingMikro, ratingMakro, ratingHeroPool, ratingAttitude);

        if (pemainIntiPerRole.containsKey(pemain.getRole())) {
            Pemain pemainIntiLama = pemainIntiPerRole.get(pemain.getRole());

            if (pemain.hitungRerataRating() > pemainIntiLama.hitungRerataRating()) {
                pemainIntiLama.status = "Pemain Cadangan";
                pemain.status = "Pemain Inti";
                pemainIntiPerRole.put(pemain.getRole(), pemain);
            } else {
                pemain.status = "Pemain Cadangan";
            }
        } else {
            pemain.status = "Pemain Inti";
            pemainIntiPerRole.put(pemain.getRole(), pemain);
        }
        promoteBackupPlayers();
        System.out.println("\nRating pemain berhasil diupdate.");
    }

    private void promoteBackupPlayers() {
        for (Role role : Role.values()) {
            if (!pemainIntiPerRole.containsKey(role)) {
                Pemain bestBackup = null;
                for (Pemain pemain : pemainMap.values()) {
                    if (pemain.getRole() == role && pemain.getStatus().equals("Pemain Cadangan")) {
                        if (bestBackup == null || pemain.hitungRerataRating() > bestBackup.hitungRerataRating()) {
                            bestBackup = pemain;
                        }
                    }
                }
                if (bestBackup != null) {
                    bestBackup.status = "Pemain Inti";
                    pemainIntiPerRole.put(role, bestBackup);
                }
            }
        }
    }

    public void updatePemain(String uid, String nama, String ign, Role role) {
        Pemain pemain = pemainMap.get(uid);
        if (pemain == null) {
            throw new IllegalArgumentException("\nPemain dengan UID " + uid + " tidak ditemukan.");
        }
        pemain.nama = nama;
        pemain.ign = ign;
        pemain.role = role;

        updatePemainInti(pemain);
        System.out.println("\nPemain berhasil diupdate.");
    }

    private void updatePemainInti(Pemain pemain) {
        if (pemain.getStatus().equals("Pemain Inti")) {
            if (!pemainIntiPerRole.containsKey(pemain.getRole())) {
                pemainIntiPerRole.put(pemain.getRole(), pemain);
            } else {
                Pemain pemainIntiLama = pemainIntiPerRole.get(pemain.getRole());
                if (pemain.hitungRerataRating() > pemainIntiLama.hitungRerataRating()) {
                    pemainIntiLama.status = "Pemain Cadangan";
                    pemainIntiPerRole.put(pemain.getRole(), pemain);
                }
            }
        } else {
            if (pemainIntiPerRole.containsKey(pemain.getRole()) && pemainIntiPerRole.get(pemain.getRole()).getUid().equals(pemain.getUid())) {
                pemainIntiPerRole.remove(pemain.getRole());
            }
        }
    }

    public void hapusPemain(String uid) {
        Pemain pemain = pemainMap.remove(uid);
        if (pemain == null) {
            throw new IllegalArgumentException("\nPemain dengan UID " + uid + " tidak ditemukan.");
        }

        for (Role role : pemainIntiPerRole.keySet()) {
            if (pemainIntiPerRole.get(role).getUid().equals(uid)) {
                pemainIntiPerRole.remove(role);
                break;
            }
        }
        promoteBackupPlayers();
        System.out.println("\nPemain dengan UID " + uid + " berhasil dihapus.");
    }

    public void cariPemainBerdasarkanRole(Role role) {
        System.out.println("\nPemain dengan role " + role + ":");
        boolean found = false;
        for (Pemain pemain : pemainMap.values()) {
            if (pemain.getRole() == role) {
                System.out.println("\n----------------------------------------------------");
                System.out.println(pemain);
                System.out.println("----------------------------------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("\nTidak ada pemain dengan role " + role + ".");
        }
    }

    public void cariPemainBerdasarkanUid(String uid) {
        Pemain pemain = pemainMap.get(uid);
        if (pemain == null) {
            throw new IllegalArgumentException("\nPemain dengan UID " + uid + " tidak ditemukan.");
        }
        System.out.println("\n----------------------------------------------------");
        System.out.println(pemain);
        System.out.println("----------------------------------------------------");
    }

    public void tampilkanSemuaPemain() {
        if (pemainMap.isEmpty()) {
            System.out.println("\nTidak ada pemain yang tersedia.");
            return;
        }
        for (Pemain pemain : pemainMap.values()) {
            System.out.println("\n----------------------------------------------------");
            System.out.println(pemain);
            System.out.println("----------------------------------------------------");
        }
    }

    private double getValidRating(Scanner scanner, String prompt) {
        double rating;
        while (true) {
            System.out.print(prompt);
            try {
                rating = Double.parseDouble(scanner.nextLine());
                if (rating < 1 || rating > 10) {
                    throw new IllegalArgumentException("Rating harus berada dalam rentang 1 hingga 10.");
                }
                return rating;
            } catch (NumberFormatException e) {
                System.out.println("\nInput tidak valid. Silakan masukkan angka.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

// Kelas Utama dengan method main
class ManajemenTimEsport {
    private static Tim tim;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            tim = new Tim();

            while (true) {
                Date HariSekarang = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("'Waktu Saat Ini: 'dd.MM.yyyy 'pukul' hh:mm:ss a zzz");

                System.out.println("\n+-----------------------------------------------------+");
                System.out.println("         MANAGEMENT TIM E-SPORT MOBILE LEGENDS        ");
                System.out.println("+-----------------------------------------------------+");
                System.out.println(ft.format(HariSekarang));
                System.out.println("1. Tambah Pemain");
                System.out.println("2. Update Rating Pemain");
                System.out.println("3. Update Pemain");
                System.out.println("4. Hapus Pemain");
                System.out.println("5. Cari Pemain Berdasarkan Role");
                System.out.println("6. Cari Pemain Berdasarkan UID");
                System.out.println("7. Tampilkan Semua Pemain");
                System.out.println("8. Keluar");
                System.out.print("Pilih menu (1-8): ");
                String pilihan = scanner.nextLine();

                try {
                    switch (pilihan) {
                        case "1":
                            String uid = getValidUid(scanner);
                            if (tim.getPemainMap().containsKey(uid)) {
                                System.out.println("\nPemain dengan UID " + uid + " sudah ada.");
                                break;
                            }
                            System.out.print("Masukkan Nama Pemain: ");
                            String nama = scanner.nextLine().trim();
                            System.out.print("Masukkan IGN Pemain: ");
                            String ign = scanner.nextLine().trim();
                            Role role = getValidRole(scanner);
                            tim.tambahPemain(uid, nama, ign, role, scanner);
                            break;

                        case "2":
                            String uidRating = getValidUid(scanner);
 if (!tim.getPemainMap().containsKey(uidRating)) {
                                System.out.println("\nPemain dengan UID " + uidRating + " tidak ditemukan.");
                                break;
                            }
                            tim.updateRatingPemain(tim.getPemainMap().get(uidRating), scanner);
                            break;

                        case "3":
                            String uidUpdate = getValidUid(scanner);
                            if (!tim.getPemainMap().containsKey(uidUpdate)) {
                                throw new IllegalArgumentException("\nPemain dengan UID " + uidUpdate + " tidak ditemukan.");
                            }
                            System.out.print("Masukkan Nama Pemain: ");
                            String namaUpdate = scanner.nextLine().trim();
                            System.out.print("Masukkan IGN Pemain: ");
                            String ignUpdate = scanner.nextLine().trim();
                            Role roleUpdate = getValidRole(scanner);
                            tim.updatePemain(uidUpdate, namaUpdate, ignUpdate, roleUpdate);
                            break;

                        case "4":
                            String uidHapus = getValidUid(scanner);
                            try {
                                tim.hapusPemain(uidHapus);
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                            break;

                        case "5":
                            Role roleCari = getValidRole(scanner);
                            tim.cariPemainBerdasarkanRole(roleCari);
                            break;

                        case "6":
                            String uidCari = getValidUid(scanner);
                            try {
                                tim.cariPemainBerdasarkanUid(uidCari);
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                            break;

                        case "7":
                            tim.tampilkanSemuaPemain();
                            break;

                        case "8":
                            System.out.println("\nTerima kasih telah menggunakan program ini. Sampai jumpa!");
                            return;

                        default:
                            System.out.println("\nPilihan tidak valid. Silakan coba lagi.");
                    }
                } catch (Exception e) {
                    System.out.println("\nTerjadi kesalahan: " + e.getMessage());
                }
            }
        }
    }

    private static String getValidUid(Scanner scanner) {
        String uid;
        while (true) {
            System.out.print("\nMasukkan UID Pemain (8-9 karakter): ");
            uid = scanner.nextLine().trim();
            if (uid.length() < 8 || uid.length() > 9) {
                System.out.println("UID harus terdiri dari 8 atau 9 karakter.");
            } else if (!uid.matches("[a-zA-Z0-9]+")) {
                System.out.println("UID hanya boleh terdiri dari angka dan huruf.");
            } else {
                return uid;
            }
        }
    }

    private static Role getValidRole(Scanner scanner) {
        int roleChoice;
        while (true) {
            System.out.println("\nPilih Role:");
            System.out.println("1. Jungler");
            System.out.println("2. Explaner");
            System.out.println("3. Goldlaner");
            System.out.println("4. Midlaner");
            System.out.println("5. Roamer");
            System.out.print("Silahkan dipilih: ");
            try {
                roleChoice = Integer.parseInt(scanner.nextLine());
                return Role.fromInt(roleChoice);
            } catch (NumberFormatException e) {
                System.out.println("\nInput tidak valid. Silakan masukkan angka.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}