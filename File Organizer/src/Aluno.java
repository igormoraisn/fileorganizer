/* Classe Aluno para a implementação de Organizadores
 * Igor Nascimento dos Santos
 * Estrutura da Dados II
 */
import java.nio.ByteBuffer;

public class Aluno {
	public static final long LENGTH = 157;
	private int matricula;	// 4 bytes
	private String nome;	// 50 bytes
	private String endereco;	// 60 bytes
	private short idade;	// 2 bytes
	private String sexo;	// 1 byte
	private String email;	// 40 bytes
	// Construtor para escrita, recebe dados
	public Aluno(int matricula, String nome, String endereco, short idade, String sexo, String email) {
		this.matricula = matricula;
		this.idade = idade;
		this.nome = this.corrigirTamanho(nome, 50);
		this.endereco =  this.corrigirTamanho(endereco, 60);
		this.sexo =  this.corrigirTamanho(sexo, 1);
		this.email =  this.corrigirTamanho(email, 40);
	}
	// Construtor para leitura, recebe ByteBuffer
	public Aluno(ByteBuffer buf) {
		this.matricula = buf.getInt();
		byte[] b_nome = new byte[50];
		buf.get(b_nome);
		this.nome = new String(b_nome);
		byte[] b_endereco = new byte[60];
		buf.get(b_endereco);
		this.endereco = new String(b_endereco);
		this.idade = buf.getShort();
		byte[] b_sexo = new byte[1];
		buf.get(b_sexo);
		this.sexo = new String(b_sexo);
		byte[] b_email = new byte[40];
		buf.get(b_email);
		this.email = new String(b_email);
	}
	public String corrigirTamanho(String s, int tam) {		// Método que corrige o tamanho das Strings para o número de bytes determinado
		int len = s.length();
		if(len < tam) {
			for(int i=len;i<tam;i++)
				s = s + " ";
		}
		else 
			s = s.substring(0,tam); 				// Se a String for maior que o determinado, o método substring corta-a
		return s;
	
	}
	// Método para gerar o Buffer de bytes com todos os dados do Aluno
	public ByteBuffer getBuffer() {
		ByteBuffer buf = ByteBuffer.allocate(157);
		buf.putInt(this.matricula);
		buf.put(this.nome.getBytes());
		buf.put(this.endereco.getBytes());
		buf.putShort(this.idade);
		buf.put(this.sexo.getBytes());
		buf.put(this.email.getBytes());
		buf.flip();
		return buf;
	}
	// Este método foi criado para facilitar a visualização dos registros
	public void retornaAluno() {
		System.out.println("Nome: " + this.nome);
		System.out.println("Matrícula: " + this.matricula);
		System.out.println("Email: " + this.email);
		System.out.println("Sexo: " + this.sexo);
		System.out.println("Idade: " + this.idade);
		System.out.println("Endereço: " + this.endereco);
	}
	public int getMatricula() {
		return matricula;
	}
	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public short getIdade() {
		return idade;
	}
	public void setIdade(short idade) {
		this.idade = idade;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
