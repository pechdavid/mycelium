package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.command.CommandHandler
import java.net.URL
import util.Try

/**
 * Created: 4/13/13 10:52 PM
 */
class DownloadUseCase extends CommandHandler {

  def handle = {
    case req: AddNewLink =>

      val downloadedUrl = new URL(req.url)

      val host = DomainHostRepository.singleton.loadCreateByHost(downloadedUrl.getHost)

      for (l <- req.links;
           res = Try(new URL(downloadedUrl, l))
           if (res.isSuccess)
      ) {

        host.incrementForHost(res.get)
      }

      DomainHostRepository.singleton.save(host)
  }
}
