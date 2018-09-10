/* Organizador de registros utilizando o Método de Brent
 * Igor Nascimento dos Santos
 * Estrutura de Dados II
 */
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.IOException;

public class OrganizadorBrent implements IFileOrganizer{
	private static final long LENGTH = 11;
	private FileChannel canal;
	private RandomAccessFile raf;
	public OrganizadorBrent(String arqName) throws IOException{
		File file = new File(arqName);
		raf = new RandomAccessFile(file, "rw");
		this.canal = raf.getChannel();
		// Se o arquivo estiver vazio, preenche todo o arquivo 
		if(this.canal.size() == 0){
			Aluno vazio = new Aluno(0, " ", " ", (short)0, " ", " ");
			for(int i=0;i<LENGTH;i++){
				this.canal.write(vazio.getBuffer(), i*Aluno.LENGTH);
			}
		}
	}
	public boolean addReg(Aluno a)throws IOException{
		/* O método addReg da classe OrganizadorBrent funciona da seguinte forma:
		 * Calcula-se o hash e obtém-se a posição do registro, se for vazio insere o registro lá
		 * Senão, faz-se um cálculo para descobrir se haverá um maior custo de acesso em mover o registro que está alocado
		 * Ou inserir o registro que será adicionado em outra posição, com a função de incremento
		 */
		ByteBuffer add = a.getBuffer(), buf;
		int matricula, matric, cont;
		long hash, inc, res, resolucao;
		matricula = add.getInt();
		System.out.println(matricula);
		hash = calculaHash(matricula);
		inc = calculaIncremento(matricula);
		buf = ByteBuffer.allocate((int)Aluno.LENGTH);
		this.canal.read(buf, hash*Aluno.LENGTH);
		buf.flip();
		matric = buf.getInt();
		// Testa se a posição original do registro é vazia
		if(matric == 0 || matric == -1){
			this.canal.write(a.getBuffer(), hash*Aluno.LENGTH);
			return true;
		}
		// Resolvendo a colisão
		else {
			cont = 0;
			res = hash;
			while(matric != 0){
				buf = ByteBuffer.allocate((int)Aluno.LENGTH);
				res = (inc + res) % LENGTH;
				this.canal.read(buf, res*Aluno.LENGTH);
				buf.flip();
				matric = buf.getInt();
				// Conferindo se o registro foi removido anteriormente
				if(matric == -1) matric = 0;
				else cont++;
				// Se já acessou o número total de registros, não há posição vazia
				if(cont == LENGTH){
					System.out.println("O conjunto de registros está completo !");
					return false;
				}
			}
			/* O método testaOpção fará o cálculo de acessos
			 * E retornará a posição em que o registro a deve ser inserido
			 */
			resolucao = testaOpcao(cont, res, hash, a);
			this.canal.write(a.getBuffer(), resolucao*Aluno.LENGTH);
		}
		return true;
	}
	public Aluno getReg(int matricula) throws IOException{
		/* O método getReg recebe uma posição do getPosition, enviando a matrícula do registro
		 * Se o valor retornado for -2, significa que o registro não foi encontrado
		 * Neste caso, retorna-se null para uma melhor utilização da interface gráfica
		 * Senão, lê-se o registro, mostra na tela e o envia como retorno
		 */
		long pos = getPosition(matricula);
		if(pos == -2) return null;
		else {
			ByteBuffer buf = ByteBuffer.allocate((int)Aluno.LENGTH);
			this.canal.read(buf, pos*Aluno.LENGTH);
			buf.flip();
			Aluno a = new Aluno(buf);
			a.retornaAluno();
			return a;
		}
	}
	public Aluno delReg(int matricula) throws IOException{
		/* O método delReg recebe a posição do registro a ser removido, enviando a matrícula do mesmo
		 * Se o valor retornado for -2, assim como no getReg, retorna-se null, pois o registro não existe
		 * Senão, escreve-se um registro com todos os dados vazios e matrícula -1 na posição do registro a ser removido
		 * Emite a mensagem de confirmação, mostra o registro removido na tela e o retorna
		 */
		
		Aluno vazio = new Aluno(-1, " ", " ", (short)1, " ", " "), retorno;
		long pos = getPosition(matricula);
		ByteBuffer del;
		if(pos == -2){
			return null;
		}
		else {
			del = ByteBuffer.allocate((int)Aluno.LENGTH);
			this.canal.read(del, pos*Aluno.LENGTH);
			del.flip();
			retorno = new Aluno(del);
			this.canal.write(vazio.getBuffer(), pos*Aluno.LENGTH);
			System.out.println("O registro foi removido com sucesso!");
			retorno.retornaAluno();
			return retorno;
		}
	}
	public long getPosition(int matricula) throws IOException{
		/* O método getPosition retorna a posição do registro cuja matrícula é passada como argumento
		 * Se na posição que for gerada pela função hash houver um registro diferente, busca-se somando o incremento ao hash
		 * Senão retorna a posição gerada pela função hash
		 */
		
		int matric, contador = 0;
		long hash, inc, res;
		ByteBuffer get = ByteBuffer.allocate((int)Aluno.LENGTH);
		hash = calculaHash(matricula);
		inc = calculaIncremento(matricula);
		this.canal.read(get, hash*Aluno.LENGTH);
		get.flip();
		matric = get.getInt();
		if(matric != matricula){
			res = hash;
			while(matric != matricula){
				get = ByteBuffer.allocate((int)Aluno.LENGTH);
				res = (inc + res) % LENGTH;
				this.canal.read(get, res*Aluno.LENGTH);
				get.flip();
				matric = get.getInt();
				contador++;
				if(contador == LENGTH){
					System.out.println("O registro não foi encontrado!");
					return -2;
				}
			}
			return res;
		}
		else return hash;	
	}
	public long testaOpcao(int opcao1, long pos, long hash, Aluno a) throws IOException{
		/* O método testaOpção é o método que calula o menor número de acessos ao ocorrer uma colisão
		 * Ele recebe o número de acessos para a inserção do novo registro, com os incrementos
		 * Assim como o hash desse registro e a posição que ele seria inserido
		 * Logo em seguida, calcula os acessos na mudança do registro que está lá com seu devido incremento
		 * Com a quantidade de acessos à cada opção, realiza-se a operação com menor número de acessos
		 * Se o número de acessos for o mesmo, opta-se por deixar o registro que está lá e achar uma nova posição para o novo
		 * Ao fim, retorna-se a posição em que o novo registro será inserido
		 */
		
		
		ByteBuffer buf, muda, buffer;
		int matricula, cont = 0;
		long inc, res;
		buf = ByteBuffer.allocate((int)Aluno.LENGTH);
		this.canal.read(buf, hash*Aluno.LENGTH);
		buf.flip();
		matricula = buf.getInt();
		inc = calculaIncremento(matricula);
		res = hash;
		while(matricula != 0){
			buffer = ByteBuffer.allocate((int)Aluno.LENGTH);
			res = (inc + res) % LENGTH;
			this.canal.read(buffer, res*Aluno.LENGTH);
			buffer.flip();
			matricula = buffer.getInt();
			if(matricula == -1) matricula = 0;
			else cont++;
		}
		if(cont >= opcao1){
			return pos;
		}
		else {
			// Muda o registro que está alocado de posição
			muda = ByteBuffer.allocate((int)Aluno.LENGTH);
			this.canal.read(muda, hash*Aluno.LENGTH);
			muda.flip();
			this.canal.write(muda, res*Aluno.LENGTH);
			return hash;
		}
	}
	public long calculaHash(int matricula){
		return matricula % LENGTH;
	}
	public long calculaIncremento(int matricula){
		return (matricula % (LENGTH-2)) + 1;
	}
}
