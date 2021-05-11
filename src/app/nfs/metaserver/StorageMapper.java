package nfs.metaserver;

import nfs.shared.StorageLocation;

/* O storage mapper faz o mapeamento do storageID para um objeto com a informação
 * sobre o storage, que permite ao cliente contactá-lo.
 * - Tem que permitir gerar o storageID
 * - Tenho que definir como vou mapear (storageId -> StorageInfo)
 * - Posso guardar aqui uma estrutura de dados que associa uma cookie que só o 
 *   storage conhece ao caminho que gere. (StorageInfo). Depois devo ter que
 *   criar vários maps, para permitir chegar ao StorageInfo tendo por base a
 *   cookie e o storageId.
 * - Tenho que criar aqui uma estrutura parecida com o StorageLocation, a
 *   StorageInfo com toda a info gerida por este objeto, e que sabe gerar o
 *   respetivo StorageLocation.
 */
public class StorageMapper {
	
	private static class StorageInfo {
		// tenho que ver se vale a pena guardar o mount point para além de toda
		// a info descrita acima.
	}
}