import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoktorEkrani extends JFrame {

    private Doktor doktor;
    private JTable tablo;
    private DefaultTableModel model;

    public DoktorEkrani(Doktor doktor) {
        this.doktor = doktor;

        setTitle("Doktor Paneli - Sayın " + doktor.getUnvan() + " " + doktor.getAd() + " " + doktor.getSoyad());
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlUst = new JPanel();
        pnlUst.setBackground(new Color(44, 62, 80));
        JLabel lblBaslik = new JLabel("Randevu Listem");
        lblBaslik.setForeground(Color.WHITE);
        lblBaslik.setFont(new Font("Arial", Font.BOLD, 20));
        pnlUst.add(lblBaslik);
        add(pnlUst, BorderLayout.NORTH);

        String[] kolonlar = {"Tarih", "Saat", "Hasta Adı Soyadı"};
        model = new DefaultTableModel(kolonlar, 0);
        tablo = new JTable(model);
        
        JScrollPane sp = new JScrollPane(tablo);
        add(sp, BorderLayout.CENTER);

        randevulariListele();

        JButton btnCikis = new JButton("ÇIKIŞ YAP");
        btnCikis.setBackground(Color.RED);
        btnCikis.setForeground(Color.WHITE);
        btnCikis.addActionListener(e -> {
            new LoginEkrani().setVisible(true);
            this.dispose();
        });
        
        JPanel pnlAlt = new JPanel();
        pnlAlt.add(btnCikis);
        add(pnlAlt, BorderLayout.SOUTH);
    }

    private void randevulariListele() {
        model.setRowCount(0);
        String doktorTamAd = doktor.getUnvan() + " " + doktor.getAd() + " " + doktor.getSoyad();
        
        List<Randevu> randevular = HastaneSistemi.getRandevularByDoktor(doktorTamAd);

        for (Randevu r : randevular) {
            Object[] satir = {r.getTarih(), r.getSaat(), r.getHastaAdSoyad()};
            model.addRow(satir);
        }
        
        if (randevular.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Henüz kayıtlı bir randevunuz bulunmamaktadır.");
        }
    }
}