/* Organizador Sequencial de Registros
 * Igor Nascimento dos Santos
 * Estrutura de Dados II
 */

import java.nio.channels.FileChannel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

public class OrganizadorSequencial implements IFileOrganizer {
	private FileChannel canal;
	private RandomAccessFile raf;
	
	// Contrutor que cria um arquivo de escrita e leitura com o nome passado por String
	public OrganizadorSequencial(String arqName) throws FileNotFoundException{
		File file = new File(arqName);
		raf = new RandomAccessFile(file, "rw");
		this.canal = raf.getChannel();
	}
	
	public boolean addReg(Aluno a) throws IOException{
		ByteBuffer buf = a.getBuffer();
		/* O método addReg tem a função de adicionar novos registros, organizando-os sequencialmente desde a inserção
		 * Aqui são feitas 3 condições:
		 * Se não houver nenhum registro
		 * Se houver um registro no arquivo
		 * Se houver mais que um registro no arquivo
		 */
		
		if(this.canal.size() == 0){
			this.canal.write(a.getBuffer(), this.canal.size());
			return true;
		}
		else if(this.canal.size() == 157){
			ByteBuffer twoReg = ByteBuffer.allocate(4);
			this.canal.read(twoReg, 0);
			twoReg.flip();
			// Fazendo a comparação entre o registro a ser inserido e o registro já armazenado
			if(buf.getInt() > twoReg.getInt()){
				this.canal.write(a.getBuffer(), this.canal.size());
			}
			else {
				ByteBuffer buffer = ByteBuffer.allocate(157);
				this.canal.read(buffer, 0);
				buffer.flip();
				this.canal.write(buffer, 157);
				this.canal.write(a.getBuffer(), 0);
			}
		return true;
		}
		else {
				ByteBuffer buffer;	
				int matric = buf.getInt();
				long tamanho = returnPosition(matric);
				if(tamanho == -1){
					for(long pos=this.canal.size()-157; pos>=0;pos-=Aluno.LENGTH) {
						buffer = ByteBuffer.allocate(157);
						this.canal.read(buffer, pos);
						buffer.flip();
						this.canal.write(buffer, pos+Aluno.LENGTH);
					}
					this.canal.write(a.getBuffer(), 0);
					return true;
				}
				else {
					for(long pos=this.canal.size()-157; pos>tamanho;pos-=Aluno.LENGTH) {
						buffer = ByteBuffer.allocate(157);
						this.canal.read(buffer, pos);
						buffer.flip();
						this.canal.write(buffer, pos+157);
					}
					this.canal.write(a.getBuffer(), tamanho+157);
					return true;
				}
			}
		}
	public long getPosition(int matricula) throws IOException{
		ByteBuffer busca;
		//long upper = this.canal.size()/157, lower = 1, middle, posição;
		
		/* No método getPosition, foram feitas duas implementações de busca
		 * Uma busca sequencial e uma busca binária
		 * A busca binária demonstrou ser extremamente mais eficiente
		 * Seguem as duas implementações, sendo que a binária está comentada
		 */
		
		long size = this.canal.size(), cont = -1;
		for(long pos=0; pos<size;pos+=Aluno.LENGTH) {
			busca = ByteBuffer.allocate(4);
			this.canal.read(busca, pos);
			busca.flip();
			if(matricula == busca.getInt()){ 
				cont = pos;
			}
		}
		return cont;
		
		/*int matriculaComp;
		while(lower <= upper){
			middle = (lower + upper)/2;
			busca = ByteBuffer.allocate(4);
			this.canal.read(busca, (middle*Aluno.LENGTH)-Aluno.LENGTH);
			busca.flip();
			matriculaComp = busca.getInt();
			if(matricula == matriculaComp){
				posição = (middle*Aluno.LENGTH)-Aluno.LENGTH;
				return posição;
			}
			else if(matricula > matriculaComp){
				lower = middle + 1;
			}
			else {
				upper = middle - 1;
			}
		}
		return -1;*/
	}
	
	public Aluno getReg(int matricula) throws IOException {
		/* O método getReg do Organizador Sequencial segue os seguintes príncipios:
		 * Chama o método getPosition, enviando a matrícula como argumento e recebendo a posição do registro
		 * Se a posição recebida for -1, significa que a variável cont não foi modificada
		 * Sendo assim, o registro correspondente à matrícula dada não foi encontrado
		 * Senão, lê se o buffer na posição indicada, mostra-se os dados do registro e o manda como retorno
		 */
		
		long pos = this.getPosition(matricula);
		if(pos == -1){
			System.out.println("A matrícula " + matricula + " não está cadastrada em nosso banco de dados!");
			return null;
		}
		else {
			ByteBuffer buf = ByteBuffer.allocate((int)Aluno.LENGTH);
			this.canal.read(buf, pos);
			buf.flip();
			Aluno a = new Aluno(buf);
			a.retornaAluno();
			return a;
		}
	}
	public Aluno delReg(int matricula) throws IOException{
		/* O método delReg da classe OrganizadorSequencial decorre do seguinte modo:
		 * Obtem-se a posição do registro a ser removido com o método getPosition
		 * Se o retorno for null, retorna-se null
		 * Esse retorno foi escolhido para uma melhor vizualização da interface gráfica
		 * Senão, move-se os registros de trás para a frente, um por um até a posição do registro a ser removido
		 * Retira-se 157 bytes do arquivo com o método truncate
		 * Mostra-se os dados do registro e retorna o objeto
		 */
		
		ByteBuffer buffer;
		long pos = this.getPosition(matricula);
		Aluno b = getReg(matricula);
		if(b == null) return null;
		else{
			System.out.println("Esse registro será deletado:");
			for(long posicao = pos; posicao < this.canal.size()-157; posicao+=Aluno.LENGTH) {
				buffer = ByteBuffer.allocate(157);
				this.canal.read(buffer, posicao+157);
				buffer.flip();
				this.canal.write(buffer, posicao);
			}
			b.retornaAluno();
			long posit = this.canal.size() - 157;
			this.canal.truncate(posit);
			return b;
		}
	}
	public long returnPosition(int matricula) throws IOException{
		/* O método returnPosition tem o simples objetivo de ao receber uma matrícula,
		 * verificar qual a posição em que esse registro deve ser inserido
		 * Ele faz a busca e retorna a posição o último registro de chave menor que o que será inserido
		 * O retorno será -1 se não houver nenhum registro de chave menor
		 * Neste caso o registro deverá ser inserido na posição 0
		 * Este método é essencial para a organização sequencial dos registros
		 */
		
		long size = this.canal.size(), cont = -1;
		for(long pos=0; pos<size;pos+=Aluno.LENGTH) {
			ByteBuffer buf = ByteBuffer.allocate(4);
			this.canal.read(buf, pos);
			buf.flip();
			if(matricula > buf.getInt()){ 
				cont = pos;
			}
		}
	return cont;
	}
}
