package io.mosip.registration.clientmanager.repository;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import io.mosip.registration.clientmanager.dao.BlocklistedWordDao;
import io.mosip.registration.clientmanager.entity.BlocklistedWord;

@RunWith(MockitoJUnitRunner.class)
public class BlocklistedWordRepositoryTest {

    @Mock
    private BlocklistedWordDao blocklistedWordDao;

    @InjectMocks
    private BlocklistedWordRepository blocklistedWordRepository;

    @Test
    public void testSaveBlocklistedWord_Success() throws JSONException {
        JSONObject jsonObject = mock(JSONObject.class);

        when(jsonObject.getString("word")).thenReturn("restricted");
        when(jsonObject.getBoolean("isActive")).thenReturn(true);

        blocklistedWordRepository.saveBlocklistedWord(jsonObject);

        verify(blocklistedWordDao, times(1)).insert(Mockito.any(BlocklistedWord.class));
    }

    @Test(expected = JSONException.class)
    public void testSaveBlocklistedWord_InvalidJson() throws JSONException {
        JSONObject jsonObject = mock(JSONObject.class);

        when(jsonObject.getString("word")).thenThrow(new JSONException("Key 'word' is missing"));

        blocklistedWordRepository.saveBlocklistedWord(jsonObject);
    }
}
