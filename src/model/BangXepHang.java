package model;

public class BangXepHang extends NguoiChoi {

	private float tongSoDiem;
	private float tongDiemDoiThu;
	private int tongSoNuocDiTranThang;
	private int tongSoNuocDiTranThua;
	private int soTranDauDaChoi;
	private int soTranThang, soTranThua;
	public BangXepHang(String tenDangNhap) {
		super(tenDangNhap);
	}

	public float getTongSoDiem() {
		return tongSoDiem;
	}

	public void setTongSoDiem(float tongSoDiem) {
		this.tongSoDiem = tongSoDiem;
	}

	public float getTongDiemDoiThu() {
		return tongDiemDoiThu;
	}

	public void setTongDiemDoiThu(float TongDiemDoiThu) {
		this.tongDiemDoiThu = tongDiemDoiThu;
	}

	public int getTongSoNuocDiTranThang() {
		return tongSoNuocDiTranThang;
	}

	public void setTongSoNuocDiTranThang(int tongSoNuocDiTranThang) {
		this.tongSoNuocDiTranThang = tongSoNuocDiTranThang;
	}

	public int getTongSoNuocDiTranThua() {
		return tongSoNuocDiTranThua;
	}

	public void setTongSoNuocDiTranThua(int tongSoNuocDiTranThua) {
		this.tongSoNuocDiTranThua = tongSoNuocDiTranThua;
	}

	public int getSoTranDauDaChoi() {
		return soTranDauDaChoi;
	}

	public void setSoTranDauDaChoi(int soTranDauDaChoi) {
		this.soTranDauDaChoi = soTranDauDaChoi;
	}

	public int getSoTranThang() {
		return soTranThang;
	}

	public void setSoTranThang(int soTranThang) {
		this.soTranThang = soTranThang;
	}

	public int getSoTranThua() {
		return soTranThua;
	}

	public void setSoTranThua(int soTranThua) {
		this.soTranThua = soTranThua;
	}

}