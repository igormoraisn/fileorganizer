/* Interface para Organizadores
 * Igor Nascimento dos Santos
 * Estrutura de Dados II
 */
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileOrganizer {
	public boolean addReg(Aluno a) throws IOException;
	public Aluno getReg(int matricula) throws FileNotFoundException, IOException;
	public Aluno delReg(int matricula) throws IOException;
}
