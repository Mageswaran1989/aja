#include <iostream>
#include <string>

class media_player
{
  public: 
    virtual void play(std::string audio_type, std::string file_name) {}
};

class advanced_media_player
{
  public:
    virtual void play_vlc(std::string file_name) {}
    virtual void play_mp4(std::string file_name) {}
};

class vlc_player : public advanced_media_player
{
  public:
    void play_vlc(std::string file_name)
    {
      std::cout << "Playing vlc fine name : "
                << file_name
                << "\n";
    }

    void play_mp4(std::string file_name)
    {
      std::cout << "Can't Play mp4 file name : "
                << file_name
                << "\n";
    }
};

class mp4_player : public advanced_media_player
{
  public:
    void play_vlc(std::string file_name)
    {
      std::cout << "Can't play vlc file name : "
                << file_name
                << "\n";
    }
  
    void play_mp4(std::string file_name)
    {
      std::cout << "Playing mp4 fine name : "
                << file_name
                << "\n";
    }
};

class media_adapter
{
  private:
    advanced_media_player* m_advanced_media_player;
  
  public:
    media_adapter(std::string audio_type)
    {
      if (audio_type == "vlc")
      {
        m_advanced_media_player = new vlc_player();
      }
      else if (audio_type == "mp4")
      {
        m_advanced_media_player = new mp4_player();
      }
    }

    void play(std::string audio_type, std::string file_name)
    {
      if(audio_type == "vlc")
      {
        m_advanced_media_player->play_vlc(file_name);
      }
      else if(audio_type == "mp4")
      {
        m_advanced_media_player->play_mp4(file_name);
      }
    }
};

class audio_player : public media_player
{
  media_adapter* m_media_adapter;

  public:
    void play(std::string audio_type, std::string file_name)
    {
      //inbuild support to play mp3 files
      if(audio_type == "mp3")
      {
        std::cout << "Playing mp3 file name :"
                  << file_name
                  << "\n";
      }
      else if (audio_type == "vlc" || audio_type == "mp4")
      {
	 m_media_adapter = new media_adapter(audio_type);
         m_media_adapter->play(audio_type, file_name);
      }
      else
      {
        std::cout << "Invalid format ! \n";
      }
    }
};

int main(void)
{
  audio_player ap;

  ap.play("mp3", "beyond the horizon.mp3");
  ap.play("mp4", "alone.mp4");
  ap.play("vlc", "far far away.vlc");
  ap.play("avi", "mind me.avi");
}



      

  


